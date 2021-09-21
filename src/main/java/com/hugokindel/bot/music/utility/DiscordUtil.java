package com.hugokindel.bot.music.utility;

import com.hugokindel.bot.music.MusicBot;
import net.dv8tion.jda.api.entities.Member;

public class DiscordUtil {
    public static String mention(Member member) {
        return "<@" + member.getId() + ">";
    }

    public static String mention(String id) {
        return "<@" + id + ">";
    }

    public static String mentionCreator() {
        return mention(MusicBot.get().config.creatorId);
    }
}
