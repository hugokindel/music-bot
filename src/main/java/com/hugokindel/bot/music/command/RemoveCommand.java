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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slash.Tag("remove")
@Slash.Command(name = "remove", description = "Efface la piste voulue de la file d'attente.", options = {
        @Option(name = "index", description = "L'index de la piste.", type = OptionType.STRING, required = true)
})
public class RemoveCommand {
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

        String option = message.getOptionsAsString();
        int i;

        try {
            i = Integer.parseInt(option);

            if (i >= 1) {
                if (i <= channelManager.trackScheduler.queue.size()) {
                    ConcurrentLinkedQueue<AudioTrack> queue = new ConcurrentLinkedQueue<>();

                    Iterator<AudioTrack> iterator = channelManager.trackScheduler.queue.iterator();
                    int i2 = 1;

                    while (iterator.hasNext()) {
                        if (i == i2) {
                            iterator.next();
                        } else {
                            queue.add(iterator.next());
                        }
                        i2++;
                    }

                    channelManager.trackScheduler.queue = queue;

                    message.sendEmbed(String.format("La piste audio à l'index %d a bien été effacé.", i));
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