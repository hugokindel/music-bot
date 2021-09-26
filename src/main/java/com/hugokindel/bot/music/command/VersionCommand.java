package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.common.Discord;
import com.hugokindel.bot.music.MusicBot;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


@Slash.Tag("version")
@Slash.Command(name = "version", description = "Affiche le numéro de version.")
public class VersionCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Version " + MusicBot.VERSION + " !");
        eb.setFooter("FORX-BOT par Forx.");
        eb.setColor(Discord.getRandomColor());
        message.sendEmbed(eb.build());
    }

    public static String getTitle() {
        return "Version";
    }
}
