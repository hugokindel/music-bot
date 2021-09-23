package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.AnyMessage;
import com.hugokindel.bot.common.Discord;
import com.hugokindel.common.cli.print.Out;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.time.temporal.ChronoUnit;

@Slash.Tag("ping")
@Slash.Command(name = "ping", description = "Évalue la latence du robot.")
public class PingCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handlePing(new AnyMessage(event));
    }

    public static void handlePing(AnyMessage message) {
        if (message.interaction != null) {
            message.interaction.reply("Le ping du serveur distant est: ...\nDemandé par " + Discord.mention(message.user)).flatMap(v ->
                    message.messageChannel.retrieveMessageById(message.messageChannel.getLatestMessageId()).flatMap(w ->
                            v.editOriginalFormat("Le ping du serveur distant est: %d ms\nDemandé par " + Discord.mention(message.user), message.timeCreated.until(w.getTimeCreated(), ChronoUnit.MILLIS))
                    )
            ).queue();
        } else if (message.message != null) {
            message.message.reply("Le ping du serveur distant est: ...\nDemandé par " + Discord.mention(message.user)).flatMap(v ->
                v.editMessageFormat("Le ping du serveur distant est: %d ms\nDemandé par " + Discord.mention(message.user), message.timeCreated.until(v.getTimeCreated(), ChronoUnit.MILLIS))
            ).queue();
        }
    }
}