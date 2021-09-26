package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("info")
@Slash.Command(name = "info", description = "Information sur le robot.")
public class InfoCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        message.sendEmbed(String.format(
                "J'ai été créé par %s dans le but de répondre aux problèmes des bots musicaux sur la plateforme. PRO PLAYER un jour, PRO PLAYER toujours.\n" +
                "Merci à %s pour le logo\n" +
                "%s",
                Discord.mentionCreator(),
                Discord.mention(MusicBot.WANGA_ID),
                VersionCommand.getVersion()
        ));
    }

    public static String getTitle() {
        return "Info";
    }
}