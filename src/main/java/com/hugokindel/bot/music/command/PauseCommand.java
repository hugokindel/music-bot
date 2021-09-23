package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.AnyMessage;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("pause")
@Slash.Command(name = "pause", description = "Met le son actuel en pause.")
public class PauseCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handlePause(new AnyMessage(event));
    }

    public static void handlePause(AnyMessage message) {
        if (!Discord.checkInGuild(message) ||
            !Discord.checkInVoiceChannel(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        if (!Discord.checkSongPlaying(message, channelManager) ||
            !Discord.checkSongNotPaused(message, channelManager)) {
            return;
        }

        channelManager.trackScheduler.player.setPaused(true);

        message.sendAnswerAskedBy("Le son actuel va être mis en pause.");
    }
}