package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.AnyMessage;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("loop")
@Slash.Command(name = "loop", description = "Active la boucle du son en cour.")
public class LoopCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handleLoop(new AnyMessage(event));
    }

    public static void handleLoop(AnyMessage message) {
        if (!Discord.checkInGuild(message) ||
            !Discord.checkInVoiceChannel(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        if (!Discord.checkSongPlaying(message, channelManager)) {
            return;
        }

        if (!channelManager.trackScheduler.looping) {
            message.sendAnswerAskedBy("Lecture en boucle de `" + channelManager.trackScheduler.player.getPlayingTrack().getInfo().title + "`.");
        } else {
            message.sendAnswerAskedBy("Désactivation de la lecture en boucle.");
        }

        channelManager.trackScheduler.looping = !channelManager.trackScheduler.looping;
    }
}