package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.common.AnyMessage;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


@Slash.Tag("version")
@Slash.Command(name = "version", description = "Affiche la version du robot.")
public class VersionCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handleVersion(new AnyMessage(event));
    }

    public static void handleVersion(AnyMessage message) {
        message.sendAnswer(getVersion());
    }

    public static String getVersion() {
        return "Version: " + MusicBot.VERSION;
    }
}
