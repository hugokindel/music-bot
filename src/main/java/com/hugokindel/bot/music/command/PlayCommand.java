// TODO: Spotify artist (best songs)
// TODO: Spotify podcasts

package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.AnyMessage;
import com.hugokindel.bot.common.Discord;
import com.hugokindel.common.cli.print.Out;
import com.hugokindel.common.utility.StringUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.model_objects.specification.*;
import net.azzerial.slash.annotations.Option;
import net.azzerial.slash.annotations.OptionType;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

@Slash.Tag("play")
@Slash.Command(name = "play", description = "Joue un son.", options = {
        @Option(name = "requête", description = "URL/nom du son.", type = OptionType.STRING, required = true)
})
public class PlayCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handlePlay(new AnyMessage(event));
    }

    public static void handlePlay(AnyMessage message) {
        if (!Discord.checkInGuild(message) ||
            !Discord.checkInVoiceChannel(message) ||
            !Discord.checkHasOption(message)) {
            return;
        }

        message.sendAnswer("Traitement de la commande...");

        while (message.answerId == null) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    if (search.contains("/track/")) {
                        String[] initialParsed = search.split("/track/");
                        if (initialParsed.length == 2) {
                            String[] finalParsed = initialParsed[1].split("\\?");
                            Track request = MusicBot.get().spotifyApi.getTrack(finalParsed[0]).build().execute();
                            search = "ytsearch: " + request.getArtists()[0].getName() + " " + request.getName();
                        }
                    } else if (search.contains("/playlist/")) {
                        String[] initialParsed = search.split("/playlist/");
                        if (initialParsed.length == 2) {
                            String[] finalParsed = initialParsed[1].split("\\?");
                            Playlist request = MusicBot.get().spotifyApi.getPlaylist(finalParsed[0]).build().execute();
                            Paging<PlaylistTrack> tracks = request.getTracks();

                            for (int i = 0; i < tracks.getItems().length; i++) {
                                Track request2 = MusicBot.get().spotifyApi.getTrack(tracks.getItems()[i].getTrack().getId()).build().execute();
                                search = "ytsearch: " + request2.getArtists()[0].getName() + " " + request2.getName();
                                doSearch(channelManager, search, message, false);
                            }

                            return;
                        }
                    } else if (search.contains("/album/")) {
                        String[] initialParsed = search.split("/album/");
                        if (initialParsed.length == 2) {
                            String[] finalParsed = initialParsed[1].split("\\?");
                            Album request = MusicBot.get().spotifyApi.getAlbum(finalParsed[0]).build().execute();
                            Paging<TrackSimplified> tracks = request.getTracks();

                            for (int i = 0; i < tracks.getItems().length; i++) {
                                search = "ytsearch: " + tracks.getItems()[i].getArtists()[0].getName() + " " + tracks.getItems()[i].getName();
                                doSearch(channelManager, search, message, false);
                            }

                            return;
                        }
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

        doSearch(channelManager, search, message, isUrl);
    }

    public static void doSearch(ChannelMusicManager channelManager, String query, AnyMessage message, boolean isUrl) {
        MusicBot.get().playerManager.loadItemOrdered(channelManager, query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                Out.println("Track loaded");

                if (!channelManager.trackScheduler.playing) {
                    if (message != null) {
                        message.appendToMessageAskedByUser("Début de la lecture de `" + track.getInfo().title + "`.");
                    }
                } else {
                    if (message != null) {
                        message.appendToMessageAskedByUser("Ajout à la file d'attente de `" + track.getInfo().title + "`.");
                    }
                }

                if (!channelManager.connected) {
                    channelManager.connect();
                }

                channelManager.trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (!isUrl) {
                    trackLoaded(playlist.getTracks().get(0));
                    return;
                }

                for (int i = 0; i < playlist.getTracks().size(); i++) {
                    AudioTrack track = playlist.getTracks().get(i);

                    if (!channelManager.trackScheduler.playing) {
                        if (message != null) {
                            if (message.answerId != null) {
                                message.appendToMessageAskedByUser("Début de la lecture de `" + track.getInfo().title + "`.");
                            } else {
                                message.editAnswerAskedByUser("Début de la lecture de `" + track.getInfo().title + "`.");
                            }
                        }
                    } else {
                        if (message != null) {
                            if (message.answerId != null) {
                                message.appendToMessageAskedByUser("Ajout à la file d'attente de `" + track.getInfo().title + "`.");
                            } else {
                                message.editAnswerAskedByUser("Ajout à la file d'attente de `" + track.getInfo().title + "`.");
                            }
                        }
                    }

                    if (!channelManager.connected) {
                        channelManager.connect();
                    }

                    channelManager.trackScheduler.queue(playlist.getTracks().get(i));
                }
            }

            @Override
            public void noMatches() {
                if (message != null) {
                    if (message.answerId != null) {
                        message.appendToMessageAskedByUser("Impossible de trouver le son voulu !");
                    } else {
                        message.sendAnswer("Impossible de trouver le son voulu !");
                    }
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();

                if (message != null) {
                    if (message.answerId != null) {
                        message.appendToMessageAskedByUser("Impossible de jouer le son voulu !");
                    } else {
                        message.sendAnswer("Impossible de jouer le son voulu !");
                    }
                }
            }
        });
    }
}