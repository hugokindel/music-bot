package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.utility.DiscordMessage;
import com.hugokindel.bot.music.utility.DiscordUtil;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class UnknownCommand {
    public static void handleUnknown(DiscordMessage message) {
        message.sendAnswerToUser("la commande `/" + message.command + "` est inconnu !\n" +
                "Tu peux voir la liste des commandes avec `/help`");
    }
}