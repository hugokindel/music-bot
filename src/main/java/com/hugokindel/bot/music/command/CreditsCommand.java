package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("credits")
@Slash.Command(name = "credits", description = "Affiche les crédits.")
public class CreditsCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        message.sendEmbed(String.format(
                "Développé par %s.\n" +
                "Logo créé par %s.\n" +
                "Remerciements spécial à %s et tous les membres du serveur des **PRO PLAYERS**.",
                Discord.mentionCreator(),
                Discord.mention(MusicBot.WANGA_ID),
                Discord.mention(MusicBot.KAASTIEL_ID)
        ));
    }

    public static String getTitle() {
        return "Crédits";
    }
}