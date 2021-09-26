// TODO: Spotify artist (best songs)

package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.Discord;
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

@Slash.Tag("play")
@Slash.Command(name = "play", description = "Joue un son.", options = {
        @Option(name = "requête", description = "URL/nom du son.", type = OptionType.STRING, required = true)
})
public class PlayCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        if (!Discord.checkInGuild(message) ||
            !Discord.checkInVoiceChannel(message) ||
            !Discord.checkHasOption(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        String search = message.getOptionsAsString();

        while (search.charAt(0) == ' ') {
            search = search.substring(1);
        }

        boolean isUrl = StringUtil.isUrl(search);
        String thumbnailUrl = null;

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
                            isUrl = false;
                            if (request.getAlbum().getImages().length > 0) {
                                thumbnailUrl = request.getAlbum().getImages()[0].getUrl();
                                message.thumbnailUrl = thumbnailUrl;
                            }
                        }
                    } else if (search.contains("/playlist/")) {
                        String[] initialParsed = search.split("/playlist/");
                        if (initialParsed.length == 2) {
                            String[] finalParsed = initialParsed[1].split("\\?");
                            Playlist request = MusicBot.get().spotifyApi.getPlaylist(finalParsed[0]).build().execute();
                            Paging<PlaylistTrack> tracks = request.getTracks();
                            Image[] playlistImages = MusicBot.get().spotifyApi.getPlaylistCoverImage(finalParsed[0]).build().execute();
                            isUrl = false;
                            if (playlistImages.length > 0) {
                                thumbnailUrl = playlistImages[0].getUrl();
                                message.thumbnailUrl = thumbnailUrl;
                            }

                            for (int i = 0; i < tracks.getItems().length; i++) {
                                if (!MusicBot.get().getGuildManager(message.guild).hasChannelManager(message.member.getVoiceState().getChannel())) {
                                    return;
                                }

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
                            isUrl = false;
                            if (request.getImages().length > 0) {
                                thumbnailUrl = request.getImages()[0].getUrl();
                                message.thumbnailUrl = thumbnailUrl;
                            }

                            for (int i = 0; i < tracks.getItems().length; i++) {
                                if (!MusicBot.get().getGuildManager(message.guild).hasChannelManager(message.member.getVoiceState().getChannel())) {
                                    return;
                                }

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
                        if (tracks.getItems()[0].getAlbum().getImages().length > 0) {
                            thumbnailUrl = tracks.getItems()[0].getAlbum().getImages()[0].getUrl();
                            message.thumbnailUrl = thumbnailUrl;
                        }
                        search = "ytsearch: " + tracks.getItems()[0].getArtists()[0].getName() + " " + tracks.getItems()[0].getName();
                    } else {
                        message.sendErrorEmbed("Impossible de trouver le son voulu !");
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

    public static void play(Guild guild, VoiceChannel voiceChannel, String query) {
        doSearch(MusicBot.get().getGuildManager(guild).getChannelManager(voiceChannel), query, null, true);
    }

    public static void doSearch(ChannelMusicManager channelManager, String query, CommandMessage message, boolean isUrl) {
        MusicBot.get().playerManager.loadItemOrdered(channelManager, query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (!MusicBot.get().getGuildManager(message.guild).hasChannelManager(message.member.getVoiceState().getChannel())) {
                    return;
                }

                if (!channelManager.trackScheduler.playing) {
                    if (message != null) {
                        message.appendAndSendEmbed(String.format(
                                "Début de la lecture de `%s`.",
                                track.getInfo().title
                        ));
                    }
                } else {
                    if (message != null) {
                        message.appendAndSendEmbed(String.format(
                                "Ajout à la file d'attente de `%s`.",
                                track.getInfo().title
                        ));
                    }
                }

                if (!channelManager.connected) {
                    channelManager.connect();
                }

                channelManager.trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (!MusicBot.get().getGuildManager(message.guild).hasChannelManager(message.member.getVoiceState().getChannel())) {
                    return;
                }

                if (!isUrl) {
                    trackLoaded(playlist.getTracks().get(0));
                    return;
                }

                for (int i = 0; i < playlist.getTracks().size(); i++) {
                    if (!MusicBot.get().getGuildManager(message.guild).hasChannelManager(message.member.getVoiceState().getChannel())) {
                        return;
                    }

                    AudioTrack track = playlist.getTracks().get(i);

                    if (!channelManager.trackScheduler.playing) {
                        if (message != null) {
                            message.appendAndSendEmbed(String.format(
                                    "Début de la lecture de `%s`.",
                                    track.getInfo().title
                            ));
                        }
                    } else {
                        if (message != null) {
                            message.appendAndSendEmbed(String.format(
                                    "Ajout à la file d'attente de `%s`.",
                                    track.getInfo().title
                            ));
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
                if (!MusicBot.get().getGuildManager(message.guild).hasChannelManager(message.member.getVoiceState().getChannel())) {
                    return;
                }

                if (message != null) {
                    // TODO: Add sound name
                    message.appendAndSendEmbed("Impossible de trouver le son voulu !", true);
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (!MusicBot.get().getGuildManager(message.guild).hasChannelManager(message.member.getVoiceState().getChannel())) {
                    return;
                }

                exception.printStackTrace();

                if (message != null) {
                    // TODO: Add sound name
                    message.appendAndSendEmbed("Impossible de jouer le son voulu !", true);
                }
            }
        });
    }

    public static String getTitle() {
        return "Lecture";
    }
}