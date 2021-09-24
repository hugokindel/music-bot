package com.hugokindel.bot.common;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

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

    public static boolean checkInGuild(AnyMessage message) {
        if (message.guild == null) {
            message.sendAnswerToUser("tu dois être dans un serveur pour appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkInVoiceChannel(AnyMessage message) {
        VoiceChannel channel = message.member.getVoiceState().getChannel();

        if (channel == null) {
            message.sendAnswerToUser("tu dois être dans un salon audio pour appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkHasOptions(AnyMessage message, int n) {
        if (message.options.size() < n) {
            message.sendAnswerToUser("au moins une option est manquante pour pouvoir appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkHasOption(AnyMessage message) {
        return checkHasOptions(message, 1);
    }

    public static boolean checkSongPlaying(AnyMessage message, ChannelMusicManager channelManager) {
        if (!channelManager.trackScheduler.playing) {
            message.sendAnswerToUser("un son doit être en train de jouer pour appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkSongNotPaused(AnyMessage message, ChannelMusicManager channelManager) {
        if (channelManager.trackScheduler.player.isPaused()) {
            message.sendAnswerToUser("un son doit être en train de jouer pour appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkSongPaused(AnyMessage message, ChannelMusicManager channelManager) {
        if (!channelManager.trackScheduler.player.isPaused()) {
            message.sendAnswerToUser("un son doit être en pause pour appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkIsAdmin(AnyMessage message) {
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

        message.sendAnswerToUser("tu n'a pas les permissions pour appeler cette commande !");
        return false;
    }

    public static boolean checkIsOwner(AnyMessage message) {
        if (message.user.getId().equals(MusicBot.get().config.creatorId)) {
            return true;
        }

        message.sendAnswerToUser("tu n'a pas les permissions pour appeler cette commande !");
        return false;
    }

    public static MessageEmbed createEmbed(String title, String message) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(title);
        eb.setDescription(message);
        eb.setFooter("- FORX-BOT par Forx");

        if (MusicBot.get().random.nextInt(2) == 0) {
            eb.setColor(MusicBot.COLOR_PINK);
        } else {
            eb.setColor(MusicBot.COLOR_GREEN);
        }

        return eb.build();
    }
}
