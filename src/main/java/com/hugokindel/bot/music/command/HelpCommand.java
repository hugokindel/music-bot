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
                "Je suis un robot de musique développé spécifiquement pour le serveur des **PRO PLAYERS** et suis capable de jouer de la musique dans tous vos salon vocaux simultanément.\n" +
                "\n" +
                "En cas de soucis ou d'idées, veuillez contactez " + Discord.mentionCreator() + ".\n" +
                "\n" +
                "Voici les commandes que vous pouvez effectuer:"
        );

        eb.addField(new MessageEmbed.Field(
                "/play <requête>",
                "Si aucune piste n'est en cours de lecture, joue la piste ou la playlist demandé sinon la rajoute à la file d'attente.\n" +
                "Il est possible de jouer des pistes depuis *YouTube*, *SoundCloud*, *Spotify* ainsi qu'un certains nombre de format audio par lien direct (MP3, FLAC, WAV, MKV, MP4, OGG) et même des streams *Twitch*.\n" +
                "Il existe des préfixes utilisable pour spécifier sur quel site effectuer une recherche: `ytsearch:`, `scsearch:` et `spsearch:`.\n" +
                "Une requête est obligatoire.\n" +
                "Exemple: `/play scsearch: Lil Nas X` (recherche Lil Nas X sur SoundCloud).",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/pause",
                "Si une piste est en cours de lecture, la met en pause sinon la reprends.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/skip <nombre>",
                "Passe le nombre voulu de pistes.\n" +
                "Le nombre n'est pas obligatoire (1 par défaut).",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/remove <index>",
                "Efface la piste voulue de la file d'attente.\n" +
                "L'index de la piste a effacer est nécessaire.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/move <index> <position>",
                "Déplace un élément de la file d'attente.\n" +
                "L'index de la piste audio a effacer ainsi que sa nouvelle position sont nécessaires.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/clear",
                "Efface la file d'attente.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/leave",
                "Force le robot à quitter le salon vocal et efface sa file d'attente.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/loop",
                "Si la piste en cours de lecture ne joue pas en boucle, active la boucle sinon la désactive.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/nowplaying",
                "Affiche la file de lecture.",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/seek <durée>",
                "Va à la durée spécifiée dans la piste en cours de lecture.\n" +
                "La durée est obligatoire et doit être au format hh:mm:ss.\n" +
                "Exemple: `/seek 1:50` (Va à 1min50 dans la piste en cours de lecture).",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/forward <durée>",
                "Avance de la durée spécifiée dans la piste en cours de lecture.\n" +
                "La durée n'est pas obligatoire (10s par défaut), si définit elle doit être au format hh:mm:ss.\n",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/backward <durée>",
                "Recule de la durée spécifiée dans la piste en cours de lecture.\n" +
                "La durée n'est pas obligatoire (10s par défaut), si définit elle doit être au format hh:mm:ss.\n",
                false
        ));

        eb.addField(new MessageEmbed.Field(
                "/shuffle",
                "Trie la file d'attente de manière aléatoire.",
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
        eb.setThumbnail("https://forx-bot.s3.eu-west-3.amazonaws.com/images/FORX-BOT-MASTER.png");

        return eb.build();
    }

    public static String getTitle() {
        return "Aide";
    }
}
