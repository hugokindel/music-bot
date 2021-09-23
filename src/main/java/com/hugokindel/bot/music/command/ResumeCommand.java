package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.AnyMessage;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("resume")
@Slash.Command(name = "resume", description = "Reprends le son actuel.")
public class ResumeCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handleResume(new AnyMessage(event));
    }

    public static void handleResume(AnyMessage message) {
        if (!Discord.checkInGuild(message) ||
            !Discord.checkInVoiceChannel(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        if (!Discord.checkSongPlaying(message, channelManager) ||
                !Discord.checkSongPaused(message, channelManager)) {
            return;
        }

        channelManager.trackScheduler.player.setPaused(false);

        message.sendAnswerAskedBy("La lecture du son en cour va reprendre.");
    }
}