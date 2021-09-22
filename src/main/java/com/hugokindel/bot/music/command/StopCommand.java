package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.music.utility.DiscordMessage;
import com.hugokindel.bot.music.utility.DiscordUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.entities.Guild;;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("stop")
@Slash.Command(name = "stop", description = "Quitte le salon vocal et efface la file d'attente.")
public class StopCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handleStop(new DiscordMessage(event));
    }

    public static void handleStop(DiscordMessage message) {
        if (!DiscordUtil.checkInGuild(message) ||
                !DiscordUtil.checkInVoiceChannel(message)) {
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(message.guild).getChannelManager(message.member.getVoiceState().getChannel());
        channelManager.messageChannel = message.messageChannel;

        if (!DiscordUtil.checkSongPlaying(message, channelManager)) {
            return;
        }

        MusicBot.get().getGuildManager(message.guild).freeChannelManager(message.member.getVoiceState().getChannel());

        message.sendAnswerAskedBy("Le robot va être déconnecté du salon vocal et sa file d'attente sera effacé.");
    }
}