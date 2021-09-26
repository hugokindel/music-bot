package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.common.Discord;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.azzerial.slash.annotations.Option;
import net.azzerial.slash.annotations.OptionType;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("skip")
@Slash.Command(name = "skip", description = "Passe le nombre voulu de pistes.", options = {
        @Option(name = "nombre", description = "Nombre de pistes à passer.", type = OptionType.STRING, required = false)
})
public class SkipCommand {
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

        if (message.options.size() == 0) {
            channelManager.trackScheduler.skipTrack();
            message.sendEmbed("La piste actuel a été passé.");
        } else {
            String option = message.getOptionsAsString();
            int n;

            try {
                n = Integer.parseInt(option);

                if (n > 0) {
                    if (n == 1) {
                        channelManager.trackScheduler.skipTrack();
                        message.sendEmbed("La piste actuel a été passé.");
                    } else {
                        int nRemoved = 0;

                        for (int i = 0; i < n - 1; i++) {
                            AudioTrack track = channelManager.trackScheduler.queue.poll();

                            if (track != null) {
                                nRemoved++;
                            }
                        }

                        channelManager.trackScheduler.skipTrack();

                        if (nRemoved > 0) {
                            message.sendEmbed(String.format("La piste actuel et %d pistes de la file d'attente ont été passés.", nRemoved));
                        } else {
                            message.sendEmbed("La piste actuel a été passé.");
                        }
                    }
                } else {
                    message.sendErrorEmbed("Le minimum possible de pistes à passer est 1 !");
                }
            } catch (Exception e) {
                message.sendErrorEmbed("Le format du nombre de pistes à passer est illisible !");
            }
        }
    }

    public static String getTitle() {
        return "Passage de la piste en cours";
    }
}