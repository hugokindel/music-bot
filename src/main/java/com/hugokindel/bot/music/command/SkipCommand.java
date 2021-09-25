package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.AnyMessage;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("skip")
@Slash.Command(name = "skip", description = "Passe le son en cour.")
public class SkipCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new AnyMessage(event));
    }

    public static void handle(AnyMessage message) {
        if (!Discord.checkInGuild(message) ||
            !Discord.checkInVoiceChannel(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        if (!Discord.checkSongPlaying(message, channelManager)) {
            return;
        }

        channelManager.trackScheduler.skipTrack();

        message.sendAnswerAskedBy("Le son actuel va être passé.");
    }
}