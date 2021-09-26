package com.hugokindel.bot.music.command.empty;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("resume")
@Slash.Command(name = "resume", description = " ")
public class ResumeCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {

    }
}