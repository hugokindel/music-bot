package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.common.Discord;
import com.hugokindel.bot.music.MusicBot;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("shutdown")
@Slash.Command(name = "shutdown", description = "Éteint le robot.")
public class ShutdownCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        if (!Discord.checkIsOwner(message)) {
            return;
        }

        message.sendEmbed("Le serveur va s'éteindre.");

        try {
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MusicBot.get().shouldShutdown.set(true);
    }

    public static String getTitle() {
        return "Fermeture";
    }
}