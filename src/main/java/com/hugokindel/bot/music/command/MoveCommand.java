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

import java.util.List;

@Slash.Tag("move")
@Slash.Command(name = "move", description = "Déplace un élément de la file d'attente.", options = {
        @Option(name = "index", description = "L'index de la piste audio.", type = OptionType.STRING, required = true),
        @Option(name = "position", description = "La position où déplacer la piste audio.", type = OptionType.STRING, required = true)
})
public class MoveCommand {
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

        if (!Discord.checkQueueNotEmpty(message, channelManager)) {
            return;
        }

        if (message.options.size() != 2) {
            message.sendErrorEmbed("Le format du nombre de pistes à passer est illisible !");
            return;
        }

        int i;
        int p;

        try {
            i = Integer.parseInt(message.options.get(0));
            p = Integer.parseInt(message.options.get(1));

            if (i >= 1) {
                if (i <= channelManager.trackScheduler.queue.size()) {
                    if (p >= 1) {
                        if (p <= channelManager.trackScheduler.queue.size() + 1) {
                            AudioTrack track = ((List<AudioTrack>)channelManager.trackScheduler.queue).get(i - 1);
                            ((List<AudioTrack>)channelManager.trackScheduler.queue).remove(i - 1);

                            if (p == channelManager.trackScheduler.queue.size() + 1) {
                                ((List<AudioTrack>)channelManager.trackScheduler.queue).add(track);
                            } else {
                                ((List<AudioTrack>)channelManager.trackScheduler.queue).add(p - 1, track);
                            }

                            message.sendEmbed(String.format("La piste audio a bien été deplacé de %d à %d.", i, p));
                        } else {
                            message.sendErrorEmbed(String.format("La position ne peut pas être supérieur à %d (la longueur de la file d'attente + 1) !", channelManager.trackScheduler.queue.size() + 1));
                        }
                    } else {
                        message.sendErrorEmbed("La position ne peut pas être inférieur à 1 !");
                    }
                } else {
                    message.sendErrorEmbed(String.format("L'index ne peut pas être supérieur à %d (la longueur de la file d'attente) !", channelManager.trackScheduler.queue.size()));
                }
            } else {
                message.sendErrorEmbed("L'index ne peut pas être inférieur à 1 !");
            }
        } catch (Exception e) {
            message.sendErrorEmbed("Le format du nombre de pistes à passer est illisible !");
        }
    }

    public static String getTitle() {
        return "Suppression d'une piste";
    }
}