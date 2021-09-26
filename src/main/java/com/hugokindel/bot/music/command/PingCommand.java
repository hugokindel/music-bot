package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.temporal.ChronoUnit;

@Slash.Tag("ping")
@Slash.Command(name = "ping", description = "Évalue la latence du robot.")
public class PingCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        message.sendEmbed(String.format(
                "La latence du robot est de %dms.", message.timeCreated.until(message.answerTimeCreated, ChronoUnit.MILLIS)
        ));
    }

    public static String getTitle() {
        return "Ping";
    }
}