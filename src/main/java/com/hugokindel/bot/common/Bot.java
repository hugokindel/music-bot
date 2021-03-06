package com.hugokindel.bot.common;

import com.hugokindel.bot.music.MusicBot;
import net.azzerial.slash.annotations.Slash;
import net.dv8tion.jda.api.EmbedBuilder;
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

            boolean isPrivate = true;

            try {
                event.getPrivateChannel();
            } catch (Exception e) {
                isPrivate = false;
            }

            String command = event.getMessage().getContentRaw();
            while (command.charAt(0) == ' ') {
                command = command.substring(1);
            }

            if (command.startsWith("/") || command.startsWith("!")) {
                boolean found = false;

                command = command.substring(1).split(" ")[0];

                for (Class<?> c : MusicBot.get().commandClasses) {
                    if (c.isAnnotationPresent(Slash.Tag.class) && !c.getName().contains(".empty")) {
                        Slash.Tag tag = c.getAnnotation(Slash.Tag.class);

                        if (command.equals(tag.value())) {
                            found = true;

                            try {
                                CommandMessage message = new CommandMessage(event);
                                message.answerTitle = (String)c.getMethod("getTitle").invoke(null);
                                c.getMethod("handle", CommandMessage.class).invoke(null, message);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                if (!found) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Commande inconnue !");
                    embedBuilder.setFooter("FORX-BOT par Forx.");
                    embedBuilder.setColor(MusicBot.COLOR_RED);
                    event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                }
            } else if (isPrivate) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Si tu essaie d'envoyer une commande, utilise le pr??fix `/` !");
                embedBuilder.setFooter("FORX-BOT par Forx.");
                embedBuilder.setColor(MusicBot.COLOR_RED);
                event.getMessage().replyEmbeds(embedBuilder.build()).queue();
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(final PrivateMessageReceivedEvent event) {
        if (type == Type.Worker) {
            if (event.getAuthor().isBot()) {
                return;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Je ne comprends pas !");
            embedBuilder.setDescription("Si tu cherche ?? envoyer une commande, tu dois contacter " + Discord.mention(MusicBot.get().host.client.getSelfUser().getId()) + " ou l'??crire directement dans le serveur en question ?? l'aide des *Slash Commands* de Discord !");
            embedBuilder.setFooter("FORX-BOT par Forx.");
            embedBuilder.setColor(MusicBot.COLOR_RED);
            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
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
