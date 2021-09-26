package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("loop")
@Slash.Command(name = "loop", description = "Active/désactive la boucle du son en cour.")
public class LoopCommand {
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

        if (!channelManager.trackScheduler.looping) {
            message.sendEmbed(String.format(
                    "Lecture en boucle de `%s`.",
                    channelManager.trackScheduler.player.getPlayingTrack().getInfo().title
            ));
        } else {
            message.sendErrorEmbed("Désactivation de la lecture en boucle.");
        }

        channelManager.trackScheduler.looping = !channelManager.trackScheduler.looping;
    }

    public static String getTitle() {
        return "Boucle";
    }
}