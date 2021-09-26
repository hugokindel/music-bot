package com.hugokindel.bot.music.command.empty;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("info")
@Slash.Command(name = "info", description = " ")
public class InfoCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {

    }
}