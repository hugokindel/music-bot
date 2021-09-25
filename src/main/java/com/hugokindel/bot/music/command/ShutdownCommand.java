package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.AnyMessage;
import com.hugokindel.bot.common.Discord;
import com.hugokindel.bot.music.MusicBot;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("shutdown")
@Slash.Command(name = "shutdown", description = "Éteint le robot.")
public class ShutdownCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new AnyMessage(event));
    }

    public static void handle(AnyMessage message) {
        if (!Discord.checkIsOwner(message)) {
            return;
        }

        message.sendAnswerToUser("le serveur va s'éteindre.");

        MusicBot.get().shouldShutdown.set(true);
    }
}