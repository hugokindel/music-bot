package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.music.utility.DiscordMessage;
import com.hugokindel.bot.music.utility.DiscordUtil;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("resume")
@Slash.Command(name = "resume", description = "Reprends le son actuel.")
public class ResumeCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handleResume(new DiscordMessage(event));
    }

    public static void handleResume(DiscordMessage message) {
        if (!DiscordUtil.checkInGuild(message) ||
            !DiscordUtil.checkInVoiceChannel(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        if (!DiscordUtil.checkSongPlaying(message, channelManager) ||
                !DiscordUtil.checkSongPaused(message, channelManager)) {
            return;
        }

        channelManager.trackScheduler.player.setPaused(false);

        message.sendAnswerAskedBy("La lecture du son en cour va reprendre.");
    }
}