package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.music.utility.DiscordMessage;
import com.hugokindel.bot.music.utility.DiscordUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("pause")
@Slash.Command(name = "pause", description = "Met le son actuel en pause.")
public class PauseCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handlePause(new DiscordMessage(event));
    }

    public static void handlePause(DiscordMessage message) {
        if (!DiscordUtil.checkInGuild(message) ||
            !DiscordUtil.checkInVoiceChannel(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        if (!DiscordUtil.checkSongPlaying(message, channelManager) ||
            !DiscordUtil.checkSongNotPaused(message, channelManager)) {
            return;
        }

        channelManager.trackScheduler.player.setPaused(true);

        message.sendAnswerAskedBy("Le son actuel va être mis en pause.");
    }
}