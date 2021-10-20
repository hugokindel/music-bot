// TODO: Twitch thumbnail
// TODO: SoundCloud thumbnail
// TODO: Cut messages when too long (play/now playing)

package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.ArrayList;
import java.util.List;

@Slash.Tag("play")
@Slash.Command(name = "play", description = "Joue la piste ou la playlist demandé sinon la rajoute à la file d'attente.", options = {
        @Option(name = "requête", description = "URL ou recherche.", type = OptionType.STRING, required = true)
})
public class PlayCommand {
    public static class PlayingMessage {
        public String start = "";

        public List<String> wait = new ArrayList<>();

        public List<String> errorsCantLoad = new ArrayList<>();

        public List<String> errorsCantFind = new ArrayList<>();

        public MessageEmbed build(ChannelMusicManager channelManager, CommandMessage message, int oldQueueLength) {
            EmbedBuilder eb = new EmbedBuilder();
            if (message.thumbnailUrl != null && !message.thumbnailUrl.isEmpty()) {
                eb.setThumbnail(message.thumbnailUrl);
            }
            eb.setTitle(getTitle());
            eb.setFooter("FORX-BOT par Forx.");
            eb.setColor(Discord.getRandomColor());
            if (!start.isEmpty()) {
                eb.addField(new MessageEmbed.Field(
                        "Lecture de la piste audio",
                        start,
                        false
                ));
            }
            if (!wait.isEmpty()) {
                StringBuilder queue = new StringBuilder();
                String[] tracks = wait.toArray(new String[0]);

                int q = oldQueueLength;

                for (int i = 0; i < wait.size(); i++) {
                    if (queue.length() > 0) {
                        queue.append("\n");
                    }

                    int num = i + 1;

                    if (start.isEmpty()) {
                        num += q;
                    }

                    queue.append(num).append(". ").append(tracks[i]);
                }

                String waitList = queue.toString();

                boolean cut = false;

                while (waitList.length() > 1020) {
                    cut = true;
                    waitList = waitList.substring(0, waitList.lastIndexOf('\n'));
                }

                if (cut) {
                    waitList += "\n...";
                }

                eb.addField(new MessageEmbed.Field(
                        "Ajout à la file d'attente",
                        waitList,
                        false
                ));
            }
            if (!errorsCantLoad.isEmpty() || !errorsCantFind.isEmpty()) {
                String errors = "";
                if (!errorsCantFind.isEmpty()) {
                    errors += String.format("- %d pistes audio introuvables.", errorsCantFind.size());
                }
                if (!errorsCantLoad.isEmpty()) {
                    if (errors.isEmpty()) {
                        errors += "\n";
                    }
                    errors += String.format("- %d pistes audio impossible à charger.", errorsCantFind.size());
                }
                eb.addField(new MessageEmbed.Field(
                        "Erreurs",
                        errors,
                        false
                ));
            }
            return eb.build();
        }
    }

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

        PlayingMessage playingMessage = new PlayingMessage();

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        int oldQueueLength = channelManager.trackScheduler.queue.size();

        String search = message.getOptionsAsString();

        while (search.charAt(0) == ' ') {
            search = search.substring(1);
        }

        boolean isUrl = StringUtil.isUrl(search);
        String thumbnailUrl;

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
                                Track request2 = MusicBot.get().spotifyApi.getTrack(tracks.getItems()[i].getTrack().getId()).build().execute();
                                search = "ytsearch: " + request2.getArtists()[0].getName() + " " + request2.getName();
                                doSearch(channelManager, search, message, false, playingMessage, oldQueueLength);
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
                                search = "ytsearch: " + tracks.getItems()[i].getArtists()[0].getName() + " " + tracks.getItems()[i].getName();
                                doSearch(channelManager, search, message, false, playingMessage, oldQueueLength);
                            }

                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (search.startsWith("spsearch:")) {
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
                    message.sendErrorEmbed("Impossible de trouver la piste voulu !");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!search.startsWith("ytsearch:") && !search.startsWith("scsearch:")) {
            search = "ytsearch:" + search;
        }

        doSearch(channelManager, search, message, isUrl, playingMessage, oldQueueLength);
    }

    public static void play(Guild guild, VoiceChannel voiceChannel, String query) {
        doSearch(MusicBot.get().getGuildManager(guild).getChannelManager(voiceChannel), query, null, true, null, 0);
    }

    public static void doSearch(ChannelMusicManager channelManager, String query, CommandMessage message, boolean isUrl, PlayingMessage playingMessage, int oldQueueLength) {
        MusicBot.get().playerManager.loadItemOrdered(channelManager, query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (message.thumbnailUrl == null) {
                    if (track.getSourceManager().getSourceName().toLowerCase().contains("youtube")) {
                        message.thumbnailUrl = String.format("https://img.youtube.com/vi/%s/mqdefault.jpg", track.getIdentifier());
                    }
                }

                if (!channelManager.trackScheduler.playing) {
                    if (message != null && playingMessage != null) {
                        playingMessage.start = track.getInfo().title;
                        message.sendEmbed(playingMessage.build(channelManager, message, oldQueueLength));
                    }
                } else {
                    if (message != null && playingMessage != null) {
                        playingMessage.wait.add(track.getInfo().title);
                        message.sendEmbed(playingMessage.build(channelManager, message, oldQueueLength));
                    }
                }

                if (!channelManager.connected) {
                    channelManager.connect();
                }

                channelManager.trackScheduler.queue(track, message.thumbnailUrl);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (message.thumbnailUrl == null) {
                    if (playlist.getTracks().get(0).getSourceManager().getSourceName().toLowerCase().contains("youtube")) {
                        message.thumbnailUrl = String.format("https://img.youtube.com/vi/%s/mqdefault.jpg", playlist.getTracks().get(0).getIdentifier());
                    }
                }

                if (!isUrl) {
                    trackLoaded(playlist.getTracks().get(0));
                    return;
                }

                for (int i = 0; i < playlist.getTracks().size(); i++) {
                    AudioTrack track = playlist.getTracks().get(i);

                    if (!channelManager.trackScheduler.playing) {
                        if (message != null && playingMessage != null) {
                            playingMessage.start = track.getInfo().title;
                            message.sendEmbed(playingMessage.build(channelManager, message, oldQueueLength));

                        }
                    } else {
                        if (message != null && playingMessage != null) {
                            playingMessage.wait.add(track.getInfo().title);
                            message.sendEmbed(playingMessage.build(channelManager, message, oldQueueLength));
                        }
                    }

                    if (!channelManager.connected) {
                        channelManager.connect();
                    }

                    channelManager.trackScheduler.queue(playlist.getTracks().get(i), message.thumbnailUrl);
                }
            }

            @Override
            public void noMatches() {
                if (message != null) {
                    playingMessage.errorsCantFind.add("");
                    message.sendEmbed(playingMessage.build(channelManager, message, oldQueueLength));
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();

                if (message != null) {
                    playingMessage.errorsCantLoad.add("");
                    message.sendEmbed(playingMessage.build(channelManager, message, oldQueueLength));
                }
            }
        });
    }

    public static String getTitle() {
        return "Lecture de pistes audio";
    }
}