// TODO: REFLECTION FOR COMMANDS
// TODO: FINISH EMBED
// TODO: AUTOMATIZE HELP ?

package com.hugokindel.bot.music;

import com.heroku.api.HerokuAPI;
import com.hugokindel.bot.common.Bot;
import com.hugokindel.bot.common.Discord;
import com.hugokindel.bot.music.audio.ChannelMusicManager;
import com.hugokindel.bot.music.audio.GuildMusicManager;
import com.hugokindel.bot.music.audio.PlayerManager;
import com.hugokindel.bot.music.command.*;
import com.hugokindel.common.BaseProgram;
import com.hugokindel.common.cli.option.annotation.Command;
import com.hugokindel.common.cli.print.In;
import com.hugokindel.common.cli.print.Out;
import com.hugokindel.common.json.Json;
import com.hugokindel.common.utility.Resources;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import net.azzerial.slash.SlashClient;
import net.azzerial.slash.SlashClientBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.reflections8.Reflections;
import org.reflections8.scanners.ResourcesScanner;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.util.ClasspathHelper;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.FilterBuilder;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Command(name = "bot", version = "0.1.0", description = "A bot for music streaming.")
public class MusicBot extends BaseProgram {
    public static final Color COLOR_RED = new Color(255, 0, 0);

    public static final Color COLOR_PINK = new Color(255, 0, 255);

    public static final Color COLOR_GREEN = new Color(0, 255, 0);

    public static final String VERSION = "0.1.0";

    public static final String WANGA_ID = "578163510027223050";

    public boolean isConfigured;

    public MusicBotConfig config;

    public Bot host;

    public List<Bot> workers = new ArrayList<>();

    public PlayerManager playerManager = new PlayerManager();

    public Map<Long, GuildMusicManager> guildManagers = new HashMap<>();

    public SpotifyApi spotifyApi;

    public ClientCredentialsRequest spotifyCredentialRequest;

    public ClientCredentials spotifyCredentials;

    public HerokuAPI herokuAPI;

    public boolean isInCloud;

    public AtomicBoolean shouldShutdown = new AtomicBoolean(false);

    public Random random = new java.util.Random(System.currentTimeMillis());

    public ScheduledExecutorService threadpool;

    public Map<Integer, Long> unusedWorkers = new HashMap<>();

    public Set<Class<?>> commandClasses;

    public void connectToSpotifyApi() {
        if (spotifyCredentials == null || spotifyCredentials.getExpiresIn() <= 0) {
            try {
                spotifyCredentials = spotifyCredentialRequest.execute();
                spotifyApi.setAccessToken(spotifyCredentials.getAccessToken());
            } catch (IOException | SpotifyWebApiException | org.apache.hc.core5.http.ParseException e) {
                Out.println("Cannot connect to Spotify API: " + e.getMessage());
                messageCreator("Impossible de se connecter à l'API de Spotify: `" + e.getMessage() + "`");
            }
        }
    }

    public void connectToHerokuApi() {
        herokuAPI = new HerokuAPI(config.herokuKey);
    }

    public GuildMusicManager getGuildManager(Guild guild) {
        long guildId = guild.getIdLong();

        if (guildManagers.containsKey(guildId)) {
            return guildManagers.get(guildId);
        }

        GuildMusicManager guildManager = new GuildMusicManager(guild);
        guildManagers.put(guildId, guildManager);
        return guildManager;
    }

    @Override
    protected int programMain(String[] args) {
        if (System.getenv("DYNO") != null) {
            isInCloud = true;
        }

        if (isInCloud) {
            Out.canUseAnsiCode(false);
        }

        Out.println("---- MUSIC-BOT ----");
        Out.println("Version: 1.0.0     ");
        Out.println("Made by Forx       ");
        Out.println("-------------------");

        loadConfig();

        if (!isConfigured) {
            Out.println("It looks like the bot is not configured!");
            Out.println("To start, you will need to change a few settings.");
            Out.println("Look into the file in `data/settings.json` for more information!");
            Out.println("Once the proper modifications have been made, you can restart this program.");
            In.next("Press ENTER to continue...");

            saveConfig();
        } else {
            try {
                initialize();

                mainLogic();

                if (!config.helpChannelId.isEmpty()) {
                    TextChannel channel = host.client.getGuildById(config.guildId).getTextChannelById(config.helpChannelId);

                    if (channel != null) {
                        if (!config.helpMessageId.isEmpty()) {
                            channel.retrieveMessageById(config.helpMessageId).flatMap(v -> v.editMessageEmbeds(HelpCommand.getHelp()))
                                    .queue(null, new ErrorHandler()
                                            .ignore(ErrorResponse.INVALID_AUTHOR_EDIT)
                                            .ignore(ErrorResponse.MISSING_ACCESS)
                                            .ignore(ErrorResponse.UNKNOWN_CHANNEL)
                                            .handle(ErrorResponse.UNKNOWN_MESSAGE, (e) -> {
                                                channel.sendMessageEmbeds(HelpCommand.getHelp()).queue();
                                            }));
                        } else {
                            Out.println("No known help message, creating a new one.");
                            channel.sendMessageEmbeds(HelpCommand.getHelp()).queue();
                        }
                    }
                }

                if (!isInCloud) {
                    Thread thread = new Thread(new CliInteractionRunnable());
                    thread.setName("CliThread");
                    thread.start();

                    while (!shouldShutdown.get()) {
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Out.println("Shutting down...");

                    try {
                        if (thread.isAlive()) {
                            thread.join(100);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Out.println("CLI thread destroyed.");

                    destroy();
                } else {
                    if (System.getenv("FORX_HEROKU_RESTART") != null) {
                        String[] split = System.getenv("FORX_HEROKU_RESTART").split(" ");

                        long curSec = TimeUnit.MILLISECONDS.toSeconds(Instant.now().toEpochMilli());

                        if (split.length == 2) {
                            long oldSec = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(split[1]));
                            long diff = curSec - oldSec;

                            if (diff < 120) {
                                Guild guild = host.client.getGuildById(config.guildId);

                                guild.retrieveMemberById(split[0]).queue(member -> {
                                    member.getUser().openPrivateChannel().queue(c -> {
                                        c.sendMessage(
                                                Discord.mention(split[0]) + ", le serveur a redémarré avec succès !"
                                        ).queue();
                                    });
                                });
                            }
                        } else if (split.length == 4) {
                            long oldSec = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(split[3]));
                            long diff = curSec - oldSec;

                            if (diff < 120) {
                                host.client.getGuildById(split[0]).getTextChannelById(split[1]).sendMessage(Discord.mention(split[2]) + ", le serveur a redémarré avec succès !").queue();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    private void initialize() {
        spotifyApi = new SpotifyApi.Builder().setClientId(config.spotifyId).setClientSecret(config.spotifySecret).build();
        spotifyCredentialRequest = spotifyApi.clientCredentials().build();

        connectToSpotifyApi();
        connectToHerokuApi();
        playerManager.init();

        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("com.hugokindel.bot.music.command"))));
        commandClasses = reflections.getSubTypesOf(Object.class);

        threadpool = Executors.newSingleThreadScheduledExecutor();
        threadpool.scheduleWithFixedDelay(() -> {
            try {
                for (int i =  0; i < workers.size(); i++) {
                    Bot worker = workers.get(i);

                    Guild guild = worker.client.getGuildById(config.guildId);
                    GuildVoiceState voiceState = worker.client.getGuildById(config.guildId).getSelfMember().getVoiceState();

                    if (!unusedWorkers.containsKey(i) && voiceState.inVoiceChannel()) {
                        VoiceChannel voiceChannel = voiceState.getChannel();

                        if (voiceChannel.getMembers().size() == 1 || !MusicBot.get().getGuildManager(guild).getChannelManager(voiceChannel).trackScheduler.playing) {
                            unusedWorkers.put(i, TimeUnit.MILLISECONDS.toSeconds(Instant.now().toEpochMilli()));
                        }
                    } else if (unusedWorkers.containsKey(i)) {
                        if (voiceState.inVoiceChannel()) {
                            VoiceChannel voiceChannel = voiceState.getChannel();

                            if (voiceChannel.getMembers().size() > 1 && MusicBot.get().getGuildManager(guild).getChannelManager(voiceChannel).trackScheduler.playing) {
                                unusedWorkers.remove(i);
                            } else {
                                long curSec = TimeUnit.MILLISECONDS.toSeconds(Instant.now().toEpochMilli());

                                if (curSec - unusedWorkers.get(i) > 300) {
                                    MusicBot.get().getGuildManager(guild).freeChannelManager(voiceChannel);
                                    unusedWorkers.remove(i);
                                }
                            }
                        } else {
                            unusedWorkers.remove(i);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 30, 5, TimeUnit.SECONDS);
    }

    private void mainLogic() throws Exception {
        host = new Bot(Bot.Type.Host, config.hostToken, Activity.listening("les envies des pro players !"));

        final SlashClient slash = SlashClientBuilder
                .create(host.client)
                .addCommand(new PlayCommand())
                .addCommand(new SkipCommand())
                .addCommand(new StopCommand())
                .addCommand(new PauseCommand())
                .addCommand(new ResumeCommand())
                .addCommand(new NowPlayingCommand())
                .addCommand(new LoopCommand())
                .addCommand(new InfoCommand())
                .addCommand(new HelpCommand())
                .addCommand(new VersionCommand())
                .addCommand(new PingCommand())
                .addCommand(new RestartCommand())
                .addCommand(new ShutdownCommand())
                .build();

        slash.getCommand("play").upsertGuild(config.guildId);
        slash.getCommand("skip").upsertGuild(config.guildId);
        slash.getCommand("stop").upsertGuild(config.guildId);
        slash.getCommand("pause").upsertGuild(config.guildId);
        slash.getCommand("resume").upsertGuild(config.guildId);
        slash.getCommand("nowplaying").upsertGuild(config.guildId);
        slash.getCommand("loop").upsertGuild(config.guildId);
        slash.getCommand("info").upsertGuild(config.guildId);
        slash.getCommand("help").upsertGuild(config.guildId);
        slash.getCommand("version").upsertGuild(config.guildId);
        slash.getCommand("ping").upsertGuild(config.guildId);
        slash.getCommand("restart").upsertGuild(config.guildId);
        slash.getCommand("shutdown").upsertGuild(config.guildId);

        for (int i = 0; i < config.workerTokens.size(); i++) {
            workers.add(new Bot(Bot.Type.Worker, config.workerTokens.get(i), Activity.listening("rien")));
        }

        Out.println("Bot initialized.");

        if (config.eventName.equals("welcome")) {
            eventWelcome();
        }
    }

    public void destroy() {
        saveConfig();

        threadpool.shutdownNow();

        host.client.getPresence().setStatus(OnlineStatus.OFFLINE);
        host.client.shutdownNow();

        for (Bot worker : workers) {
            worker.client.getPresence().setStatus(OnlineStatus.OFFLINE);
            worker.client.shutdownNow();
        }
    }

    public void messageCreator(String message) {
        if (host != null) {
            host.client.getUserById(MusicBot.get().config.creatorId).openPrivateChannel().queue(c -> {
                c.sendMessage(message).queue();
            });
        }
    }

    private void loadConfig() {
        if (isInCloud) {
            if (checkEnvVar("FORX_HOST_ID") && checkEnvVar("FORX_HOST_TOKEN") &&
                checkEnvVar("FORX_WORKER_IDS") && checkEnvVar("FORX_WORKER_TOKENS") &&
                checkEnvVar("FORX_GUILD_ID") && checkEnvVar("FORX_CREATOR_ID") &&
                checkEnvVar("FORX_SPOTIFY_ID") && checkEnvVar("FORX_SPOTIFY_SECRET") &&
                checkEnvVar("FORX_HEROKU_KEY") && checkEnvVar("FORX_HEROKU_APP_NAME")) {
                config = new MusicBotConfig();
                config.hostId = System.getenv("FORX_HOST_ID");
                config.hostToken = System.getenv("FORX_HOST_TOKEN");
                config.workerIds = Arrays.asList(System.getenv("FORX_WORKER_IDS").split(" "));
                config.workerTokens = Arrays.asList(System.getenv("FORX_WORKER_TOKENS").split(" "));
                config.guildId = System.getenv("FORX_GUILD_ID");
                config.creatorId = System.getenv("FORX_CREATOR_ID");
                config.spotifyId = System.getenv("FORX_SPOTIFY_ID");
                config.spotifySecret = System.getenv("FORX_SPOTIFY_SECRET");
                config.herokuKey = System.getenv("FORX_HEROKU_KEY");
                config.herokuAppName = System.getenv("FORX_HEROKU_APP_NAME");
                config.helpMessageId = System.getenv("FORX_HELP_MESSAGE_ID");
                config.helpChannelId = System.getenv("FORX_HELP_CHANNEL_ID");

                if (System.getenv("FORX_EVENT_NAME") != null) {
                    config.eventName = System.getenv("FORX_EVENT_NAME");
                } else {
                    config.eventName = "";
                }

                isConfigured = true;
            }
        } else if (Resources.getConfig().global.isEmpty()) {
            config = new MusicBotConfig();
        } else {
            config = Json.deserialize(Resources.getConfig().global, MusicBotConfig.class);
            if (!config.hostId.isEmpty() && !config.hostToken.isEmpty() && !config.workerIds.isEmpty() &&
                !config.workerTokens.isEmpty() && !config.guildId.isEmpty() && !config.creatorId.isEmpty() &&
                !config.spotifyId.isEmpty() && !config.spotifySecret.isEmpty() && !config.herokuKey.isEmpty() && !config.herokuAppName.isEmpty()) {
                isConfigured = true;
            }
        }
    }

    public boolean checkEnvVar(String name) {
        if (System.getenv(name) == null) {
            Out.println("Environment variable `" + name + "` missing to run correctly!");
            return false;
        }

        return true;
    }

    private void saveConfig() {
        Resources.getConfig().global = Json.serialize(config);
    }

    public static MusicBot get() {
        return (MusicBot)BaseProgram.get();
    }

    public Guild getGuild() {
        return host.client.getGuildById(config.guildId);
    }

    public void eventWelcome() {
        //TextChannel channel = host.client.getTextChannelsByName("infos", false).get(0);
        //channel.sendMessageEmbeds(HelpCommand.getHelp()).queue();

        Guild guild = host.client.getGuildById(config.guildId);
        assert guild != null;
        List<VoiceChannel> voiceChannels = guild.getVoiceChannels();
        TextChannel textChannel = guild.getTextChannelsByName("music", false).get(0);
        for (VoiceChannel voiceChannel : voiceChannels) {
            if (!voiceChannel.getName().equals("AFK")) {
                //PlayCommand.play(guild, voiceChannel, textChannel, "https://forx-bot.s3.eu-west-3.amazonaws.com/events/welcome-1.mp3");
                //PlayCommand.play(guild, voiceChannel, textChannel, "https://forx-bot.s3.eu-west-3.amazonaws.com/events/welcome-2.mp3");
                PlayCommand.play(guild, voiceChannel, "https://forx-bot.s3.eu-west-3.amazonaws.com/events/welcome-1.mp3");
                PlayCommand.play(guild, voiceChannel, "https://forx-bot.s3.eu-west-3.amazonaws.com/events/welcome-2.mp3");
            }
        }
    }

    protected static class CliInteractionRunnable implements Runnable {
        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;

            Out.println("You can now enter commands.");

            try {
                do {
                    while (!br.ready() && !MusicBot.get().shouldShutdown.get()) {
                        Thread.sleep(10);
                    }

                    if (MusicBot.get().shouldShutdown.get()) {
                        break;
                    }

                    input = br.readLine();

                    if (input.equals("shutdown") ||  input.equals("exit") || input.equals("quit")) {
                        break;
                    }
                } while (true);

                br.close();
            } catch (Exception e) {
                if (!e.getMessage().toLowerCase().contains("broken pipe")) {
                    e.printStackTrace();
                }
            }

            MusicBot.get().shouldShutdown.set(true);
        }
    }

    // Temporary code to send private messages to a few specific persons
    /*public void eventWelcome() {
        Guild guild = host.client.getGuildById(config.guildId);

        sendPrivateMessage(guild, "259438342356074496");
        sendPrivateMessage(guild, "330434030241579019");
        sendPrivateMessage(guild, "298177718845964288");
        sendPrivateMessage(guild, "194791160894586880");
        sendPrivateMessage(guild, "833837029192237126");
        sendPrivateMessage(guild, "319163214481063938");
        sendPrivateMessage(guild, "232902914929197063");
        sendPrivateMessage(guild, "331935539420856320");
        sendPrivateMessage(guild, "578163510027223050");
        sendPrivateMessage(guild, "568149216254492672");
    }

    public void sendPrivateMessage(Guild guild, String id) {
        guild.retrieveMemberById(id).queue(member -> {
            member.getUser().openPrivateChannel().queue(c -> {
                c.sendMessage(
                        "Hello, World!"
                ).queue();
            });
        });
    }*/
}
