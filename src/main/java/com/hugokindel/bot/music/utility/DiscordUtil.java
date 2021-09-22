package com.hugokindel.bot.music.utility;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.Interaction;

public class DiscordUtil {
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

    public static boolean checkInGuild(DiscordMessage message) {
        if (message.guild == null) {
            message.sendAnswerToUser("tu dois être dans un serveur pour appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkInVoiceChannel(DiscordMessage message) {
        VoiceChannel channel = message.member.getVoiceState().getChannel();

        if (channel == null) {
            message.sendAnswerToUser("tu dois être dans un salon audio pour appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkHasOptions(DiscordMessage message, int n) {
        if (message.options.size() < n) {
            message.sendAnswerToUser("au moins une option est manquante pour pouvoir appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkHasOption(DiscordMessage message) {
        return checkHasOptions(message, 1);
    }

    public static boolean checkSongPlaying(DiscordMessage message, ChannelMusicManager channelManager) {
        if (!channelManager.trackScheduler.playing) {
            message.sendAnswerToUser("un son doit être en train de jouer pour appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkSongNotPaused(DiscordMessage message, ChannelMusicManager channelManager) {
        if (channelManager.trackScheduler.player.isPaused()) {
            message.sendAnswerToUser("un son doit être en train de jouer pour appeler cette commande !");
            return false;
        }

        return true;
    }

    public static boolean checkSongPaused(DiscordMessage message, ChannelMusicManager channelManager) {
        if (!channelManager.trackScheduler.player.isPaused()) {
            message.sendAnswerToUser("un son doit être en pause pour appeler cette commande !");
            return false;
        }

        return true;
    }
}
