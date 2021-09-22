package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.utility.DiscordMessage;
import com.hugokindel.bot.music.utility.DiscordUtil;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("info")
@Slash.Command(name = "info", description = "Information sur le robot.")
public class InfoCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handleInfo(new DiscordMessage(event));
    }

    public static void handleInfo(DiscordMessage message) {
        message.sendAnswer(getInfo());
    }

    public static String getInfo() {
        return  "J'ai été créé par " + DiscordUtil.mentionCreator() + " dans le but de répondre aux problèmes des bots musicaux sur la plateforme. " +
                "PRO PLAYER un jour, PRO PLAYER toujours.\n" +
                "Merci à " + DiscordUtil.mention(MusicBot.WANGA_ID) + " pour le logo !\nVersion: " + MusicBot.VERSION;
    }
}