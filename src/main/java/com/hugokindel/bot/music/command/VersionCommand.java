package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.utility.DiscordUtil;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


@Slash.Tag("version")
@Slash.Command(name = "version", description = "Affiche la version du robot.")
public class VersionCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        event.deferReply().setContent("Version: " + MusicBot.VERSION).queue();
    }
}
