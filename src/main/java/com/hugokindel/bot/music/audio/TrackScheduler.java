package com.hugokindel.bot.music.audio;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.common.cli.print.Out;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {
    public ChannelMusicManager channelManager;

    public AudioPlayer player;

    public Queue<AudioTrack> queue;

    public Queue<String> queueThumbnails;

    public String currentThumbnail = null;

    public boolean playing;

    public boolean isSkipping;

    public boolean looping;

    public AudioTrack lastTrack;

    public TrackScheduler(ChannelMusicManager channelManager, AudioPlayer player) {
        this.channelManager = channelManager;
        this.player = player;
        this.queue = new LinkedList<>();
        this.queueThumbnails = new LinkedList<>();
    }

    public void queue(AudioTrack track) {
        queue(track, null);
    }

    public void queue(AudioTrack track, String thumbnail) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            queueThumbnails.offer(thumbnail);
        } else {
            currentThumbnail = thumbnail;
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        playing = true;

        if (!MusicBot.get().workers.get(channelManager.workerId).client.getPresence().getActivity().getName().equals(Activity.listening(track.getInfo().title).getName())) {
            MusicBot.get().workers.get(channelManager.workerId).client.getPresence().setActivity(Activity.listening(track.getInfo().title));
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        lastTrack = track;

        if (queue.isEmpty() && !looping) {
            playing = false;
            MusicBot.get().workers.get(channelManager.workerId).client.getPresence().setActivity(Activity.listening("rien"));
        } else if (endReason.mayStartNext || isSkipping) {
            if (looping) {
                player.startTrack(lastTrack.makeClone(), false);
            } else {
                player.startTrack(queue.poll(), false);
                currentThumbnail = queueThumbnails.poll();
            }
        }
    }

    public void skipTrack() {
        isSkipping = true;
        player.stopTrack();
    }

    public void shuffle()
    {
        Collections.shuffle((List<?>) queue);
    }
}