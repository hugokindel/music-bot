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
            message.sendErrorEmbed("Tu dois être dans un serveur pour utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkInVoiceChannel(CommandMessage message) {
        VoiceChannel channel = message.member.getVoiceState().getChannel();

        if (channel == null) {
            message.sendErrorEmbed("Tu dois être dans un salon audio pour utiliser cette commande !");
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
            message.sendErrorEmbed("Un son doit être en train de jouer pour utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkSongNotPaused(CommandMessage message, ChannelMusicManager channelManager) {
        if (channelManager.trackScheduler.player.isPaused()) {
            message.sendErrorEmbed("Un son doit être en train de jouer pour utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkSongPaused(CommandMessage message, ChannelMusicManager channelManager) {
        if (!channelManager.trackScheduler.player.isPaused()) {
            message.sendErrorEmbed("Un son doit être en pause pour utiliser cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkIsAdmin(CommandMessage message) {
        if (message.user.getId().equals(MusicBot.get().config.creatorId)) {
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
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(title);
        eb.setDescription(message);
        eb.setFooter("FORX-BOT par Forx.");

        if (MusicBot.get().random.nextInt(2) == 0) {
            eb.setColor(MusicBot.COLOR_PINK);
        } else {
            eb.setColor(MusicBot.COLOR_GREEN);
        }

        return eb.build();
    }

    public static MessageEmbed createEmbed(String title, String message, Color color) {
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

        return eb.build();
    }

    public static MessageEmbed createTitleOnly(String title) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(title);
        eb.setFooter("FORX-BOT par Forx.");

        return eb.build();
    }
}
