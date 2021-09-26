package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("stop")
@Slash.Command(name = "stop", description = "Quitte le salon vocal et efface la file d'attente.")
public class StopCommand {
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

        MusicBot.get().getGuildManager(message.guild).freeChannelManager(message.member.getVoiceState().getChannel());

        message.sendEmbed("Le robot va être déconnecté du salon vocal et sa file d'attente sera effacé.");
    }

    public static String getTitle() {
        return "Stop";
    }
}