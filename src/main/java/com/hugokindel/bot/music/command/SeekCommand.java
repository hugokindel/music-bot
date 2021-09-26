package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.Discord;
import com.hugokindel.common.utility.FormatUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.azzerial.slash.annotations.Option;
import net.azzerial.slash.annotations.OptionType;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("seek")
@Slash.Command(name = "seek", description = "Va à la durée spécifiée dans la piste en cours de lecture.", options = {
        @Option(name = "durée", description = "Durée voulue (au format hh:mm:ss).", type = OptionType.STRING, required = true)
})
public class SeekCommand {
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

        long position;

        try {
            position = FormatUtil.convertTimestamp(message.getOptionsAsString());
        } catch (Exception e) {
            message.sendErrorEmbed("Le format de la durée est illisible !");
            return;
        }

        AudioTrack track = channelManager.trackScheduler.player.getPlayingTrack();

        if (track.isSeekable() && !channelManager.trackScheduler.player.getPlayingTrack().getSourceManager().getSourceName().toLowerCase().contains("twitch")) {
            if (position < track.getDuration()) {
                track.setPosition(position);
                message.sendEmbedTitleOnly("La durée actuelle a bien été changée.");
            } else {
                message.sendErrorEmbed("La durée demandée est plus longue que la durée de la piste audio !");
            }
        } else {
            message.sendErrorEmbed("La lecture de cette piste audio ne peut pas être modifiée !");
        }
    }

    public static String getTitle() {
        return "Changement de la durée";
    }
}