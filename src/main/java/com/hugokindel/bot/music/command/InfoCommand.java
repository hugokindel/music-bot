package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.common.AnyMessage;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("info")
@Slash.Command(name = "info", description = "Information sur le robot.")
public class InfoCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new AnyMessage(event));
    }

    public static void handle(AnyMessage message) {
        message.sendAnswer(getInfo());
    }

    public static String getInfo() {
        return  "J'ai été créé par " + Discord.mentionCreator() + " dans le but de répondre aux problèmes des bots musicaux sur la plateforme. " +
                "PRO PLAYER un jour, PRO PLAYER toujours.\n" +
                "Merci à " + Discord.mention(MusicBot.WANGA_ID) + " pour le logo !\nVersion: " + MusicBot.VERSION;
    }
}