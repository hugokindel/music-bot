package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.Discord;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("nowplaying")
@Slash.Command(name = "nowplaying", description = "Affiche la file de lecture.")
public class NowPlayingCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        if (!Discord.checkInGuild(message) ||
            !Discord.checkInVoiceChannel(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        if (!Discord.checkSongPlaying(message, channelManager)) {
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        if (channelManager.trackScheduler.currentThumbnail != null) {
            eb.setThumbnail(channelManager.trackScheduler.currentThumbnail);
        }
        eb.setTitle(getTitle());
        eb.setFooter("FORX-BOT par Forx.");
        eb.setColor(Discord.getRandomColor());
        eb.addField(new MessageEmbed.Field(
                "Piste audio en cours",
                channelManager.trackScheduler.player.getPlayingTrack().getInfo().title,
                false
        ));
        StringBuilder queue = new StringBuilder();
        if (!channelManager.trackScheduler.queue.isEmpty()) {
            AudioTrack[] tracks = channelManager.trackScheduler.queue.toArray(new AudioTrack[0]);

            for (int i = 0; i < channelManager.trackScheduler.queue.size(); i++) {
                if (queue.length() > 0) {
                    queue.append("\n");
                }

                queue.append(i + 1).append(". ").append(tracks[i].getInfo().title);
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
                    "File d'attente",
                    waitList,
                    false
            ));
        }
        message.sendEmbed(eb.build());
    }

    public static String getTitle() {
        return "Historique de lecture";
    }
}