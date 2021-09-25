package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.common.AnyMessage;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


@Slash.Tag("version")
@Slash.Command(name = "version", description = "Affiche la version du robot.")
public class VersionCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        message.sendEmbed(getVersion());
    }

    public static String getVersion() {
        return "Nous sommes actuellement en " + MusicBot.VERSION + ".";
    }

    public static String getTitle() {
        return "Version";
    }
}
