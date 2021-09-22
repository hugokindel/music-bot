package com.hugokindel.bot.music;

import com.hugokindel.bot.music.command.*;
import com.hugokindel.bot.music.utility.DiscordMessage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

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
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (type == Type.Host) {
            if (event.getAuthor().isBot()) {
                return;
            }

            DiscordMessage message = new DiscordMessage(event);

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
                } else {
                    UnknownCommand.handleUnknown(message);
                }
            }
        }
    }
}
