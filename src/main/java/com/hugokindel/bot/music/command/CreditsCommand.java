package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("credits")
@Slash.Command(name = "credits", description = "Affiche les crédits.")
public class CreditsCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.addField(new MessageEmbed.Field(
                "Développeur",
                "- " + Discord.mentionCreator(),
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "Créateur du logo",
                "- " + Discord.mention(MusicBot.WANGA_ID),
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "Remerciements à",
                "- " + Discord.mention(MusicBot.KAASTIEL_ID) + "\n" +
                "- Tous les du serveur des **PRO PLAYERS** !",
                false
        ));

        eb.setTitle(getTitle());
        eb.setFooter("FORX-BOT par Forx.");
        eb.setColor(Discord.getRandomColor());

        message.sendEmbed(eb.build());
    }

    public static String getTitle() {
        return "Crédits";
    }
}