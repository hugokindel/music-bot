package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.music.utility.DiscordMessage;
import com.hugokindel.bot.music.utility.DiscordUtil;
import com.hugokindel.common.utility.StringUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import net.azzerial.slash.annotations.Option;
import net.azzerial.slash.annotations.OptionType;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.Interaction;

@Slash.Tag("play")
@Slash.Command(name = "play", description = "Joue un son.", options = {
        @Option(name = "requête", description = "URL/nom du son.", type = OptionType.STRING, required = true)
})
public class PlayCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handlePlay(new DiscordMessage(event));
    }

    public static void handlePlay(DiscordMessage message) {
        if (!DiscordUtil.checkInGuild(message) ||
            !DiscordUtil.checkInVoiceChannel(message) ||
            !DiscordUtil.checkHasOption(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        String search = message.getOptionsAsOne();

        while (search.charAt(0) == ' ') {
            search = search.substring(1);
        }

        boolean isUrl = StringUtil.isUrl(search);

        if (isUrl) {
            if (search.contains("spotify.com")) {
                MusicBot.get().connectToSpotifyApi();

                try {
                    String[] initialParsed = search.split("/track/");
                    if (initialParsed.length == 2) {
                        String[] finalParsed = initialParsed[1].split("\\?");
                        Track request = MusicBot.get().spotifyApi.getTrack(finalParsed[0]).build().execute();
                        search = "ytsearch: " + request.getArtists()[0].getName() + " " + request.getName();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (search.startsWith("spsearch:")) {
                MusicBot.get().connectToSpotifyApi();

                try {
                    Paging<Track> tracks = MusicBot.get().spotifyApi.searchTracks(search.substring(9)).build().execute();

                    if (tracks.getItems().length > 0) {
                        search = "ytsearch: " + tracks.getItems()[0].getArtists()[0].getName() + " " + tracks.getItems()[0].getName();
                    } else {
                        message.sendAnswer("Impossible de trouver le son voulu !");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (!search.startsWith("ytsearch:") && !search.startsWith("scsearch:")) {
                search = "ytsearch:" + search;
            }
        }

        MusicBot.get().playerManager.loadItemOrdered(channelManager, search, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (!channelManager.trackScheduler.playing) {
                    message.sendAnswerAskedBy("Début de la lecture de `" + track.getInfo().title + "`.");
                } else {
                    message.sendAnswerAskedBy("Ajout à la file d'attente de `" + track.getInfo().title + "`.");
                }

                if (!channelManager.connected) {
                    channelManager.connect();
                }

                channelManager.trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                trackLoaded(playlist.getTracks().get(0));
            }

            @Override
            public void noMatches() {
                message.sendAnswer("Impossible de trouver le son voulu !");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
                message.sendAnswer("Impossible de jouer le son voulu !");
            }
        });
    }
}