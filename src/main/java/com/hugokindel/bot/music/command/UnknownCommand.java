package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.AnyMessage;

public class UnknownCommand {
    public static void handleUnknown(AnyMessage message) {
        message.sendAnswerToUser("la commande `/" + message.command + "` est inconnu !\n" +
                "Tu peux voir la liste des commandes avec `/help`");
    }
}