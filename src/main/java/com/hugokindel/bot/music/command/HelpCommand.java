package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


@Slash.Tag("help")
@Slash.Command(name = "help", description = "Affiche un message d'aide.")
public class HelpCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        message.sendEmbed(getHelp());
    }

    public static MessageEmbed getHelp() {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setDescription(
                "Salut, je m'appelle __**FORX-BOT**__ !\n" +
                "\n" +
                "Je suis un robot de musique développé pour le serveur des **PRO PLAYERS** et suis capable de jouer de la musique dans tous vos salon vocaux simultanément.\n" +
                "\n" +
                "En cas de soucis ou de recommandations, veuillez contactez " + Discord.mentionCreator() + ".\n" +
                "\n" +
                "Voici les commandes que vous pouvez effectuer:"
        );

        eb.addField(new MessageEmbed.Field(
                "/play <requête>",
                "Si aucun son n'est en cours, joue le son ou la playlist demandé sinon le rajoute à la file d'attente.\n" +
                "Il est possible de jouer des sons *YouTube*, *SoundCloud*, *Spotify* ainsi qu'un certains nombre de format audio par lien direct (MP3, FLAC, WAV, MKV, MP4, OGG) et même des streams *Twitch*.\n" +
                "Il existe des préfix utilisable pour spécifier sur quel site effectuer une recherche: `ytsearch:`, `scsearch:` et `spsearch:`.\n" +
                "Example: `/play scsearch: Lil Nas X` (recherche Lil Nas X sur SoundCloud)",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/pause",
                "Si un son est en cours, le met en pause sinon le reprends.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/skip",
                "Passe le son en cours.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/loop",
                "Si le son en cours ne joue pas en boucle, active la boucle sinon la désactive.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/stop",
                "Force le robot à quitter le salon vocal et efface sa file d'attente.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/nowplaying",
                "Affiche le son en cours et la file d'attente.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/ping",
                "Évalue la latence du robot.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/restart",
                "Redémarre le robot.\n" +
                "**Nécessite d'être un administrateur du serveur !**",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/shutdown",
                "Éteint le robot.\n" +
                "**Nécessite d'être le créateur du robot !**",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/version",
                "Affiche le numéro de version.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/credits",
                "Affiche les crédits.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/help",
                "Affiche ce message d'aide.",
                false
        ));

        eb.setTitle(getTitle());
        eb.setFooter("FORX-BOT par Forx.");
        eb.setColor(Discord.getRandomColor());

        return eb.build();
    }

    public static String getTitle() {
        return "Aide";
    }
}
