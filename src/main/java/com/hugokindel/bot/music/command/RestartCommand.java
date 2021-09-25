package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.AnyMessage;
import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.common.Discord;
import com.hugokindel.bot.music.MusicBot;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.Instant;
import java.util.HashMap;

@Slash.Tag("restart")
@Slash.Command(name = "restart", description = "Redémarre le robot.")
public class RestartCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        if (!Discord.checkIsAdmin(message)) {
            return;
        }

        if (MusicBot.get().isInCloud) {
            message.sendEmbed("Le serveur va redémarrer, cela pourrais prendre quelques minutes...");
            HashMap<String, String> config = new HashMap<>();
            config.put("FORX_HEROKU_RESTART", message.isInGuild() ? message.guild.getId() + " " + message.messageChannel.getId() + " " + message.user.getId() + " " + Instant.now().toEpochMilli() : message.user.getId() + " " + Instant.now().toEpochMilli());
            MusicBot.get().herokuAPI.updateConfig(MusicBot.get().config.herokuAppName, config);
            MusicBot.get().herokuAPI.restartDynos(MusicBot.get().config.herokuAppName);
        } else {
            message.sendErrorEmbed("Le serveur n'est pas dans le cloud et ne peut pas redémarrer de lui-même.");
        }
    }

    public static String getTitle() {
        return "Redémarrage";
    }
}