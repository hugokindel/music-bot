package com.hugokindel.bot.music;

import com.hugokindel.bot.music.audio.GuildMusicManager;
import com.hugokindel.bot.music.audio.PlayerManager;
import com.hugokindel.bot.music.command.*;
import com.hugokindel.bot.music.utility.DiscordUtil;
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
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.io.IOException;
import java.util.*;

@Command(name = "bot", version = "0.1.0", description = "A bot for music streaming.")
public class MusicBot extends BaseProgram {
    public static final String VERSION = "0.1.0";

    public boolean isConfigured;

    public MusicBotConfig config;

    public Bot host;

    public List<Bot> workers = new ArrayList<>();

    public PlayerManager playerManager = new PlayerManager();

    public Map<Long, GuildMusicManager> guildManagers = new HashMap<>();

    public SpotifyApi spotifyApi;

    public ClientCredentialsRequest spotifyCredentialRequest;

    public ClientCredentials spotifyCredentials;

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

                //if (System.getenv("FORX_HOST_ID") == null) {
                    //while (true) {
                    //    String command = In.nextString(/*"Enter a command (e.g: help): "*/"");

                    //    if (command.equals("exit")) {
                    //        break;
                    //    }
                   // }
                //}

                while (true) {
                    System.out.println("always running program ==> " + Calendar.getInstance().getTime());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }

                destroy();
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

        playerManager.init();
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
                .build();

        slash.getCommand("play").upsertGuild(config.guildId);
        slash.getCommand("skip").upsertGuild(config.guildId);
        slash.getCommand("stop").upsertGuild(config.guildId);
        slash.getCommand("pause").upsertGuild(config.guildId);
        slash.getCommand("resume").upsertGuild(config.guildId);
        slash.getCommand("nowplaying").upsertGuild(config.guildId);
        slash.getCommand("loop").upsertGuild(config.guildId);
        slash.getCommand("info").upsertGuild(config.guildId);

        for (int i = 0; i < config.workerTokens.size(); i++) {
            workers.add(new Bot(Bot.Type.Worker, config.workerTokens.get(i), Activity.listening("rien")));
        }

        Out.println("Bot initialized.");
        Out.println("You can now enter commands.");
    }

    private void destroy() throws InterruptedException {
        saveConfig();

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
        if (System.getenv("FORX_HOST_ID") != null) {
            config = new MusicBotConfig();
            config.hostId = System.getenv("FORX_HOST_ID");
            config.hostToken = System.getenv("FORX_HOST_TOKEN");
            config.workerIds = Arrays.asList(System.getenv("FORX_WORKER_IDS").split(" "));
            config.workerTokens = Arrays.asList(System.getenv("FORX_WORKER_TOKENS").split(" "));
            config.guildId = System.getenv("FORX_GUILD_ID");
            config.creatorId = System.getenv("FORX_CREATOR_ID");
            config.spotifyId = System.getenv("FORX_SPOTIFY_ID");
            config.spotifySecret = System.getenv("FORX_SPOTIFY_SECRET");
            isConfigured = true;
        } else if (Resources.getConfig().global.isEmpty()) {
            config = new MusicBotConfig();
        } else {
            config = Json.deserialize(Resources.getConfig().global, MusicBotConfig.class);
            if (!config.hostId.isEmpty() && !config.hostToken.isEmpty() && !config.workerIds.isEmpty() &&
                !config.workerTokens.isEmpty() && !config.guildId.isEmpty() && !config.creatorId.isEmpty()) {
                isConfigured = true;
            }
        }
    }

    private void saveConfig() {
        Resources.getConfig().global = Json.serialize(config);
    }

    public static MusicBot get() {
        return (MusicBot)BaseProgram.get();
    }
}
