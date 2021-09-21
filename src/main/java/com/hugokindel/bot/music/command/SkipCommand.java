package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.music.utility.DiscordUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("skip")
@Slash.Command(name = "skip", description = "Passe le son en cour.")
public class SkipCommand {
    @Slash.Handler()
    public void callback(SlashCommandEvent event) {
        assert event.getMember() != null;

        Guild guild = event.getGuild();

        if (guild == null) {
            event.deferReply().setContent(DiscordUtil.mention(event.getMember()) + ", tu dois être dans un serveur pour appeler cette commande !").queue();
            return;
        }

        assert event.getMember().getVoiceState() != null;
        assert event.getMember().getVoiceState().getChannel() != null;

        VoiceChannel channel = event.getMember().getVoiceState().getChannel();

        if (channel == null) {
            event.deferReply().setContent(DiscordUtil.mention(event.getMember()) + ", tu dois être dans un salon audio pour appeler cette commande !").queue();
            return;
        }

        ChannelMusicManager channelManager = MusicBot.get().getGuildManager(guild).getChannelManager(channel);
        channelManager.messageChannel = event.getChannel();

        if (!channelManager.trackScheduler.playing) {
            event.deferReply().setContent(DiscordUtil.mention(event.getMember()) + ", un son doit être en train de jouer pour appeler cette commande !").queue();
            return;
        }

        channelManager.trackScheduler.skipTrack();

        event.deferReply().setContent("le son actuel va être passé.\nDemandé par: " + DiscordUtil.mention(event.getMember())).queue();
    }
}