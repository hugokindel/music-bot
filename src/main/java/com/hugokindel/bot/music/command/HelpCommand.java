package com.hugokindel.bot.music.command;

import com.hugokindel.bot.common.AnyMessage;
import com.hugokindel.bot.common.CommandMessage;
import com.hugokindel.bot.common.Discord;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


@Slash.Tag("help")
@Slash.Command(name = "help", description = "Affiche l'aide.")
public class HelpCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handle(new CommandMessage(event, getTitle()));
    }

    public static void handle(CommandMessage message) {
        message.sendEmbed(getHelp());
    }

    public static MessageEmbed getHelp() {
        return Discord.createEmbed("Aide",
                "Bonjour, je suis **FORX-BOT** !\n\n" +
                        "Je suis capable de jouer de la musique dans tous vos salon vocaux " +
                        "simultanément.\n\n" +
                        "Voici les commandes dont je dispose:\n" +
                        "`/help`: affiche ce message d'aide.\n" +
                        "`/play <requête>`: joue le son voulu (ou le rajoute à la file d'attente).\n" +
                        "Il est possible de jouer des sons YouTube, SoundCloud, Spotify ainsi qu'un certains nombre de format audio si vous avez le lien du fichier (MP3, FLAC, WAV, MKV, MP4, OGG). Les playlists et albums sur les sites mentionnés sont aussi supportés.\n" +
                        "Vous pouvez donner directement le lien d'un son à jouer.\n" +
                        "Ou vous pouvez donner une recherche qui sera effectué sur YouTube par défaut.\n" +
                        "Mais vous pouvez spécifier sur quel site faire une recherche à l'aide des préfix correspondant !\n" +
                        "Il existe: `ytsearch:`, `scsearch:` et `spsearch:`\n" +
                        "Example: `/play scsearch: Lil Nas X` (recherche Lil Nas X sur SoundCloud)\n" +
                        "`/pause`: Met la musique en cours en pause.\n" +
                        "`/resume`: Reprends la musique en cours..\n" +
                        "`/nowplaying`: Affiche des informations sur le son en cours.\n" +
                        "`/skip`: Passe le son en cours.\n" +
                        "`/loop`: Active/désactive la boucle du son en cours.\n" +
                        "`/stop`: Quitte le salon vocal et efface la file d'attente.\n" +
                        "`/version`: Affiche la version du robot.\n" +
                        "`/info`: Affiche des informations supplémentaires sur le robot.\n" +
                        "`/ping`: Évalue la latence du robot.\n" +
                        "\n" +
                        "Certaines commandes ne sont disponibles qu'aux administrateurs du serveur:\n" +
                        "`/restart`: Redémarre le robot.\n" +
                        "\n" +
                        "Il existe aussi des commandes disponible uniquement pour le développeur:\n" +
                        "`/shutdown`: Éteint le robot.\n" +
                        "\n" +
                        "Notez que la lecture de son Spotify n'est pas possible car les sons sur Spotify sont soumis à un DRM. " +
                        "Ce qui se passe en réalité est la recherche YouTube du son voulu à l'aide des métadonnée récupéré par le lien/la recherche Spotify effectuée.\n" +
                        "\n" +
                        "L'autocomplétion de mes commandes devrait toujours vous être proposé grâce à la fonctionnalité *Slash Commands* de Discord, si ce n'est pas le cas, pensez bien à utiliser `/help` pour vous rappeler des commandes !\n" +
                        "\nVous pouvez aussi m'envoyer des commandes par message privé, mais l'intérêt est limité." + "\n\n" +
                        "En cas de soucis, contactez " + Discord.mentionCreator() + ".\n\n" +
                        "Amusez-vous bien !");
    }

    public static String getTitle() {
        return "Aide";
    }
}
