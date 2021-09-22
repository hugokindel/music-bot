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

@Slash.Tag("skip")
@Slash.Command(name = "skip", description = "Passe le son en cour.")
public class SkipCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handleSkip(new DiscordMessage(event));
    }

    public static void handleSkip(DiscordMessage message) {
        if (!DiscordUtil.checkInGuild(message) ||
            !DiscordUtil.checkInVoiceChannel(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        if (!DiscordUtil.checkSongPlaying(message, channelManager)) {
            return;
        }

        channelManager.trackScheduler.skipTrack();

        message.sendAnswerAskedBy("Le son actuel va être passé.");
    }
}