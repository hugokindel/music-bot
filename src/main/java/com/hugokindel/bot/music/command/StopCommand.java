package com.hugokindel.bot.music.command;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.music.utility.DiscordUtil;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.entities.Guild;;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

@Slash.Tag("stop")
@Slash.Command(name = "stop", description = "Quitte le salon vocal et efface la file d'attente.")
public class StopCommand {
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

        if (!MusicBot.get().getGuildManager(guild).hasChannelManager(channel)) {
            event.deferReply().setContent(DiscordUtil.mention(event.getMember()) + ", un robot doit être dans ton salon vocal pour appeler cette commande !").queue();
            return;
        }

        MusicBot.get().getGuildManager(guild).freeChannelManager(channel);

        event.deferReply().setContent("Le robot va être déconnecté du salon vocal.\nDemandé par: " + DiscordUtil.mention(event.getMember())).queue();
    }
}