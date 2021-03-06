package com.hugokindel.bot.common;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Discord {
    public static String mention(Member member) {
        return "<@" + member.getId() + ">";
    }

    public static String mention(User user) {
        return "<@" + user.getId() + ">";
    }

    public static String mention(String id) {
        return "<@" + id + ">";
    }

    public static String mentionCreator() {
        return mention(MusicBot.get().config.creatorId);
    }

    public static boolean checkInGuild(CommandMessage message) {
        if (message.guild == null) {
            message.sendErrorEmbed("Tu doit être dans un serveur pour utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkInVoiceChannel(CommandMessage message) {
        VoiceChannel channel = message.member.getVoiceState().getChannel();

        if (channel == null) {
            message.sendErrorEmbed("Tu doit être dans un salon audio pour utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkHasOptions(CommandMessage message, int n) {
        if (message.options.size() < n) {
            message.sendErrorEmbed("Au moins une option est manquante pour pouvoir utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkHasOption(CommandMessage message) {
        return checkHasOptions(message, 1);
    }

    public static boolean checkSongPlaying(CommandMessage message, ChannelMusicManager channelManager) {
        if (!channelManager.trackScheduler.playing) {
            message.sendErrorEmbed("Une piste audio doit être en train de jouer pour utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkQueueNotEmpty(CommandMessage message, ChannelMusicManager channelManager) {
        if (channelManager.trackScheduler.queue.isEmpty()) {
            message.sendErrorEmbed("La file d'attente ne doit pas être vide pour utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkSongNotPaused(CommandMessage message, ChannelMusicManager channelManager) {
        if (channelManager.trackScheduler.player.isPaused()) {
            message.sendErrorEmbed("Une piste audio doit être en train de jouer pour utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkSongPaused(CommandMessage message, ChannelMusicManager channelManager) {
        if (!channelManager.trackScheduler.player.isPaused()) {
            message.sendErrorEmbed("Une piste audio doit être en pause pour utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkIsAdmin(CommandMessage message) {
        if (message.user.getId().equals(MusicBot.get().config.creatorId) ||
            message.user.getId().equals("298177718845964288") ||
            message.user.getId().equals("232902914929197063") ||
            message.user.getId().equals("319163214481063938") ||
            message.user.getId().equals("833837029192237126") ||
            message.user.getId().equals("568149216254492672") ||
            message.user.getId().equals("331935539420856320") ||
            message.user.getId().equals("898325661431758869")) {
            return true;
        } else {
            try {
                Member member = MusicBot.get().host.client.getGuildById(MusicBot.get().config.guildId).getMemberById(message.user.getId());

                if (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR)) {
                    return true;
                }
            } catch (Exception ignored) {

            }
        }

        message.sendErrorEmbed("Tu n'as pas les permissions pour utiliser cette commande !");
        return false;
    }

    public static boolean checkIsOwner(CommandMessage message) {
        if (message.user.getId().equals(MusicBot.get().config.creatorId)) {
            return true;
        }

        message.sendErrorEmbed("Tu n'as pas les permissions pour utiliser cette commande !");
        return false;
    }

    public static MessageEmbed createEmbed(String title, String message) {
        return createEmbed(title, message, null, null);
    }

    public static MessageEmbed createEmbed(String title, String message, Color color) {
        return createEmbed(title, message, color, null);
    }

    public static MessageEmbed createEmbed(String title, String message, Color color, String thumbnail) {
        EmbedBuilder eb = new EmbedBuilder();

        if (title != null && !title.isEmpty()) {
            eb.setTitle(title);
        }

        if (message != null && !message.isEmpty()) {
            eb.setDescription(message);
        }

        eb.setFooter("FORX-BOT par Forx.");

        if (color != null) {
            eb.setColor(color);
        } else {
            if (MusicBot.get().random.nextInt(2) == 0) {
                eb.setColor(MusicBot.COLOR_PINK);
            } else {
                eb.setColor(MusicBot.COLOR_GREEN);
            }
        }

        if (thumbnail != null && !thumbnail.isEmpty()) {
            eb.setThumbnail(thumbnail);
        }

        return eb.build();
    }

    public static MessageEmbed createTitleOnly(String title) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(title);
        eb.setFooter("FORX-BOT par Forx.");

        return eb.build();
    }

    public static Color getRandomColor() {
        if (MusicBot.get().random.nextInt(2) == 0) {
            return MusicBot.COLOR_PINK;
        } else {
            return MusicBot.COLOR_GREEN;
        }
    }
}
