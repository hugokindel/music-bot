package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("pause")
@Slash.Command(name = "pause", description = "Met le son en cours en pause s'il est en lecture, sinon le reprends.")
public class PauseCommand {
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

        if (channelManager.trackScheduler.player.isPaused()) {
            channelManager.trackScheduler.player.setPaused(false);
            message.sendEmbed("La lecture du son en cours va reprendre.");
        } else {
            channelManager.trackScheduler.player.setPaused(true);
            message.sendEmbed("La lecture du son en cours va être mis en pause.");
        }
    }

    public static String getTitle() {
        return "Pause/reprise de lecture";
    }
}