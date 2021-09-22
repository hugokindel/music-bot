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

@Slash.Tag("loop")
@Slash.Command(name = "loop", description = "Active la boucle du son en cour.")
public class LoopCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handleLoop(new DiscordMessage(event));
    }

    public static void handleLoop(DiscordMessage message) {
        if (!DiscordUtil.checkInGuild(message) ||
            !DiscordUtil.checkInVoiceChannel(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        if (!DiscordUtil.checkSongPlaying(message, channelManager)) {
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