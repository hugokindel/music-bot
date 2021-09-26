package com.hugokindel.bot.music.audio;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.common.cli.print.Out;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;

public class GuildMusicManager {
    public Guild guild;

    public PriorityQueue<Integer> availableWorkers;

    public Map<Long, ChannelMusicManager> channelManagers;

    public GuildMusicManager(Guild guild) {
        this.guild = guild;

        availableWorkers = new PriorityQueue<>(MusicBot.get().workers.size());
        for (int i = 0; i < MusicBot.get().workers.size(); i++) {
            availableWorkers.offer(i);
        }

        channelManagers = new HashMap<>();
    }

    public void destroy() {
        for (Map.Entry<Long, ChannelMusicManager> entry : channelManagers.entrySet()) {
            entry.getValue().destroy();
        }
    }

    public boolean hasChannelManager(VoiceChannel channel) {
        return channelManagers.containsKey(channel.getIdLong());
    }

    public ChannelMusicManager getChannelManager(VoiceChannel channel) {
        long channelId = channel.getIdLong();

        if (channelManagers.containsKey(channelId)) {
            return channelManagers.get(channelId);
        }

        if (!availableWorkers.isEmpty()) {
            int workerId = availableWorkers.poll();
            ChannelMusicManager channelManager = new ChannelMusicManager(channel, workerId, this);
            channelManagers.put(channelId, channelManager);
            return channelManager;
        }

        Out.println("Asking for more workers than available!");

        return null;
    }

    public void freeChannelManager(VoiceChannel channel) {
        long channelId = channel.getIdLong();

        if (!channelManagers.containsKey(channelId)) {
            return;
        }

        ChannelMusicManager musicManager = channelManagers.get(channelId);

        int workerId = musicManager.workerId;
        availableWorkers.offer(workerId);
        musicManager.destroy();
        channelManagers.remove(channelId);
        MusicBot.get().workers.get(workerId).client.getPresence().setActivity(Activity.listening("rien"));
    }
}