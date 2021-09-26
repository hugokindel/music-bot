package com.hugokindel.bot.music.command.empty;

import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("stop")
@Slash.Command(name = "stop", description = " ")
public class StopCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {

    }
}