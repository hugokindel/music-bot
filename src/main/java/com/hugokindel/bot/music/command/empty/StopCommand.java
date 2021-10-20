package com.hugokindel.bot.music.command.empty;

import com.hugokindel.bot.common.CommandMessage;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("stop")
@Slash.Command(name = "stop", description = " ")
public class StopCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {

    }

    public static void handle(CommandMessage message) {

    }

    public static String getTitle() {
        return "Stop";
    }
}