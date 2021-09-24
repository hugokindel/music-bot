package com.hugokindel.bot.common;

import com.hugokindel.bot.music.command.HelpCommand;
import com.hugokindel.common.cli.print.Out;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class AnyMessage {
    public Guild guild;

    public MessageChannel messageChannel;

    public User user;

    public Member member;

    public List<String> options;

    public String command;

    public OffsetDateTime timeCreated;

    public Interaction interaction;

    public Message message;

    public String answerId;

    public String precalculatedMessage;

    public AnyMessage(SlashCommandEvent event) {
        guild = event.getGuild();
        messageChannel = event.getMessageChannel();
        user = event.getUser();
        member = event.getMember();
        timeCreated = event.getTimeCreated();
        interaction = event;
        options = new ArrayList<>();
        for (int i = 0; i < event.getOptions().size(); i++) {
            options.add(event.getOptions().get(i).getAsString());
        }
        command = event.getName();
    }

    public AnyMessage(MessageReceivedEvent event) {
        guild = event.isFromGuild() ? event.getGuild() : null;
        messageChannel = event.getChannel();
        user = event.getAuthor();
        member = event.getMember();
        timeCreated = event.getMessage().getTimeCreated();
        interaction = null;
        options = new ArrayList<>();
        command = "";
        message = event.getMessage();

        String command = event.getMessage().getContentRaw();

        while (command.charAt(0) == ' ') {
            command = command.substring(1);
        }

        if (command.startsWith("/")) {
            String[] split = command.split(" ");
            this.command = split[0].substring(1);
            if (split.length > 1) {
                options.addAll(Arrays.asList(split).subList(1, split.length));
            }
        }
    }

    public String getOptionsAsOne() {
        StringBuilder r = new StringBuilder();

        for (String option : options) {
            r.append(option);
        }

        return r.toString();
    }

    // TODO: Optimize
    public void sendAnswer(String message) {
        if (interaction != null) {
            interaction.deferReply().setContent(message).flatMap(v -> v.retrieveOriginal().flatMap(w -> {
                answerId = w.getId();
                return v.retrieveOriginal();
            })).queue();
        } else {
            messageChannel.sendMessage(message).flatMap(v -> {
                answerId = v.getId();
                return v.retrieveReactionUsers("");
            }).queue();
        }
    }

    public void sendEmbed(MessageEmbed embed) {
        if (interaction != null) {
            interaction.deferReply().addEmbeds(embed).flatMap(v -> v.retrieveOriginal().flatMap(w -> {
                answerId = w.getId();
                return v.retrieveOriginal();
            })).queue();
        } else {
            messageChannel.sendMessageEmbeds(embed).flatMap(v -> {
                answerId = v.getId();
                return v.retrieveReactionUsers("");
            }).queue();
        }
    }

    public void sendEmbed(String title, String message) {
        MessageEmbed embed = Discord.createEmbed(title, message);

        if (interaction != null) {
            interaction.deferReply().addEmbeds(embed).flatMap(v -> v.retrieveOriginal().flatMap(w -> {
                answerId = w.getId();
                return v.retrieveOriginal();
            })).queue();
        } else {
            messageChannel.sendMessageEmbeds(embed).flatMap(v -> {
                answerId = v.getId();
                return v.retrieveReactionUsers("");
            }).queue();
        }
    }

    public String getAnswerId() {
        if (interaction != null) {
            return interaction.getMessageChannel().getLatestMessageId();
        } else {
            return messageChannel.getLatestMessageId();
        }
    }

    public void deleteAnswer() {
        if (interaction != null) {
            interaction.getMessageChannel().deleteMessageById(getAnswerId());
        } else {
            messageChannel.deleteMessageById(getAnswerId());
        }
    }

    public void editAnswer(String message) {
        while (answerId == null) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (interaction != null) {
            interaction.getMessageChannel().editMessageById(answerId, message).queue();
        } else {
            messageChannel.editMessageById(answerId, message).queue();
        }
    }

    public void editAnswerAskedByUser(String message) {
        editAnswer(message + "\nDemandé par: " + Discord.mention(user));
    }

    public void sendAnswerAskedBy(String message) {
        sendAnswer(message + "\nDemandé par: " + Discord.mention(user));
    }

    public void sendAnswerToUser(String message) {
        sendAnswer(Discord.mention(user) + ", " + message);
    }

    public boolean isInGuild() {
        return guild != null;
    }

    public boolean isCommand() {
        return !command.isEmpty();
    }

    public void appendToMessageAskedByUser(String message) {
        if (precalculatedMessage == null) {
            precalculatedMessage = "";
        } else {
            precalculatedMessage += "\n";
        }

        precalculatedMessage += message;
        editAnswerAskedByUser(precalculatedMessage);
    }
}
