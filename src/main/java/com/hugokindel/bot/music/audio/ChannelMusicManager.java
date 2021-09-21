package com.hugokindel.bot.music.audio;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.common.cli.print.Out;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class ChannelMusicManager {
    public Guild guild;

    public VoiceChannel channel;

    public int workerId;

    public GuildMusicManager guildManager;

    public MessageChannel messageChannel;

    public AudioPlayer audioPlayer;

    public TrackScheduler trackScheduler;

    public boolean connected;

    public ChannelMusicManager(VoiceChannel channel, int workerId, GuildMusicManager guildManager) {
        this.guild = MusicBot.get().workers.get(workerId).client.getGuildById(guildManager.guild.getIdLong());

        assert this.guild != null;

        this.channel = this.guild.getVoiceChannelById(channel.getIdLong());
        this.workerId = workerId;
        this.guildManager = guildManager;
        audioPlayer = MusicBot.get().playerManager.createPlayer();
        trackScheduler = new TrackScheduler(this, audioPlayer);
        audioPlayer.addListener(trackScheduler);
        this.guild.getAudioManager().setSendingHandler(getSendHandler());
    }

    public void destroy() {
        if (connected) {
            disconnect();
        }

        audioPlayer.destroy();
    }

    public void connect() {
        //messageChannel.sendMessage("Connexion au salon vocal `" + channel.getName() + "`").queue();
        guild.getAudioManager().openAudioConnection(channel);
        connected = true;
    }

    public void disconnect() {
        //messageChannel.sendMessage("Déconnexion du salon vocal `" + channel.getName() + "`").queue();
        guild.getAudioManager().closeAudioConnection();
        connected = false;
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(audioPlayer);
    }
}
