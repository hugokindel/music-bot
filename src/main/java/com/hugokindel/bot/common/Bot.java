package com.hugokindel.bot.common;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.bot.music.command.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class Bot extends ListenerAdapter {
    public enum Type {
        Host,
        Worker
    }


    public final static GatewayIntent[] INTENTS = {GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_VOICE_STATES};

    public Type type;

    public JDA client;

    public Bot(Type type, String token, @Nullable Activity activity) throws LoginException, InterruptedException {
        this.type = type;

        client = JDABuilder.createDefault(token, Arrays.asList(INTENTS))
                .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.EMOTE, CacheFlag.ONLINE_STATUS)
                .setActivity(activity)
                .addEventListeners(this)
                .setBulkDeleteSplittingEnabled(true)
                .build()
                .awaitReady();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (type == Type.Host) {
            if (event.getAuthor().isBot()) {
                return;
            }

            AnyMessage message = new AnyMessage(event);

            if (message.isCommand()) {
                if (message.command.equals("help")) {
                    HelpCommand.handleHelp(message);
                } else if (message.command.equals("info")) {
                    InfoCommand.handleInfo(message);
                } else if (message.command.equals("loop")) {
                    LoopCommand.handleLoop(message);
                } else if (message.command.equals("nowplaying")) {
                    NowPlayingCommand.handleNowPlaying(message);
                } else if (message.command.equals("pause")) {
                    PauseCommand.handlePause(message);
                } else if (message.command.equals("play")) {
                    PlayCommand.handlePlay(message);
                } else if (message.command.equals("resume")) {
                    ResumeCommand.handleResume(message);
                } else if (message.command.equals("skip")) {
                    SkipCommand.handleSkip(message);
                } else if (message.command.equals("stop")) {
                    StopCommand.handleStop(message);
                } else if (message.command.equals("version")) {
                    VersionCommand.handleVersion(message);
                } else if (message.command.equals("ping")) {
                    PingCommand.handlePing(message);
                } else if (message.command.equals("restart")) {
                    RestartCommand.handleRestart(message);
                } else {
                    UnknownCommand.handleUnknown(message);
                }
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(final PrivateMessageReceivedEvent event) {
        if (type == Type.Worker) {
            if (event.getAuthor().isBot()) {
                return;
            }

            event.getChannel().sendMessage(Discord.mention(event.getAuthor()) + ", je ne sais pas comment te répondre !" +
                    "\n\n" +
                    "Pour appeler une commande, tu dois contacter " + Discord.mention(MusicBot.get().host.client.getSelfUser().getId()) + " ou le faire directement sur le serveur à l'aide des Slash Commands !" +
                    "\n\n" +
                    "En cas de soucis, contactez " + Discord.mentionCreator() + ".").queue();
        }
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        // TODO: Check why it is called per voice channel (which is a performance issue) and not only once.

        boolean isWorker = false;

        for (int i = 0; i < MusicBot.get().workers.size(); i++) {
            if (MusicBot.get().workers.get(i).client.getSelfUser().getId().equals(event.getMember().getId())) {
                isWorker = true;
                break;
            }
        }

        if (isWorker) {
            MusicBot.get().getGuildManager(event.getGuild()).freeChannelManager(event.getChannelLeft());
        }
    }
}
