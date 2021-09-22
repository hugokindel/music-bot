package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.utility.DiscordMessage;
import com.hugokindel.bot.music.utility.DiscordUtil;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


@Slash.Tag("help")
@Slash.Command(name = "help", description = "Affiche l'aide.")
public class HelpCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        handleHelp(new DiscordMessage(event));
    }

    public static void handleHelp(DiscordMessage message) {
        message.sendAnswer(getHelp());
    }

    public static String getHelp() {
        return  "Bonjour, je suis **FORX-BOT** !\n\n" +
                "Je suis capable de jouer de la musique dans tous vos salon vocaux " +
                "simultanément.\n\n" +
                "Voici les commandes dont je dispose:\n" +
                "`/help`: affiche ce message d'aide.\n" +
                "`/play <requête>`: joue le son voulu (ou le rajoute à la file d'attente).\n" +
                "Il est possible de jouer des sons de YouTube, SoundCloud et Spotify.\n" +
                "Vous pouvez donner directement le lien d'un son à jouer.\n" +
                "Ou vous pouvez donner une recherche qui sera effectué sur YouTube par défaut.\n" +
                "Mais vous pouvez spécifier sur quel site faire une recherche à l'aide des préfix correspondant !\n" +
                "Il existe: `ytsearch:`, `scsearch:` et `spsearch:`\n" +
                "Example: `/play scsearch: Lil Nas X` (recherche Lil Nas X sur SoundCloud)\n" +
                "`/pause`: Met la musique en cour en pause.\n" +
                "`/resume`: Reprends la musique en cour..\n" +
                "`/nowplaying`: Affiche des informations sur le son en cours.\n" +
                "`/skip`: Passe le son en cour.\n" +
                "`/loop`: Active/désactive la boucle du son en cour.\n" +
                "`/stop`: Quitte le salon vocal et efface la file d'attente.\n" +
                "`/version`: affiche la version du robot.\n" +
                "`/info`: Affiche des informations supplémentaires sur le robot.\n" +
                "\n" +
                "Notez que la lecture de son Spotify n'est pas possible car les sons sur Spotify sont soumis à un DRM.\n" +
                "Ce qui se passe en réalité est la recherche YouTube du son voulu à l'aide des métadonnée récupéré par le lien/la recherche Spotify effectuée.\n" +
                "\n" +
                "L'autocomplétion de mes commandes devrait toujours vous être proposé grâce à la fonctionnalité Slash Commands de Discord, si ce n'est pas le cas, pensez bien à utiliser `/help` pour vous rappeler des commandes !\n" +
                "\nVous pouvez aussi m'envoyer des commandes par message privé, mais l'intérêt est limité." + "\n\n" +
                "En cas de soucis, contactez " + DiscordUtil.mentionCreator() + ".\n" +
                "Amusez-vous bien !";
    }
}
