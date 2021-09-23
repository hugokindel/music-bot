package com.hugokindel.bot.common;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void sendAnswer(String message) {
        if (interaction != null) {
            interaction.deferReply().setContent(message).queue();
        } else {
            messageChannel.sendMessage(message).queue();
        }
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
}
