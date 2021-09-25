package com.hugokindel.bot.common;

import com.hugokindel.bot.music.MusicBot;
import com.hugokindel.common.cli.print.Out;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandMessage {
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

    public String answerTitle;

    public String answerMessage;

    public OffsetDateTime answerTimeCreated;

    public CommandMessage(SlashCommandEvent event, String answerTitle) {
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
        this.answerTitle = answerTitle;

        handlingCommand();
    }

    public CommandMessage(SlashCommandEvent event) {
        this(event, "Titre inconnu");
    }

    public CommandMessage(MessageReceivedEvent event, String answerTitle) {
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
        this.answerTitle = answerTitle;

        handlingCommand();
    }

    public CommandMessage(MessageReceivedEvent event) {
        this(event, "Titre inconnu");
    }

    public boolean isInGuild() {
        return guild != null;
    }

    public boolean isCommand() {
        return !command.isEmpty();
    }

    public String getOptionsAsString() {
        StringBuilder r = new StringBuilder();

        for (String option : options) {
            r.append(option);
        }

        return r.toString();
    }

    public void sendEmbed(MessageEmbed answerEmbed) {
        handleEmbed(answerEmbed);
    }

    public void sendEmbed(String answer) {
        handleEmbed(Discord.createEmbed(answerTitle, answer));
    }

    public void sendEmbed(String answer, Color color) {
        handleEmbed(Discord.createEmbed(answerTitle, answer, color));
    }

    public void appendAndSendEmbed(String answer) {
        if (answerMessage == null) {
            answerMessage = "";
        } else {
            answerMessage += "\n";
        }

        answerMessage += answer;

        handleEmbed(Discord.createEmbed(answerTitle, answerMessage));
    }

    public void sendErrorEmbed(String text) {
        handleEmbed(Discord.createEmbed(text, null, MusicBot.COLOR_RED));
    }

    private void handlingCommand() {
        MessageEmbed embed = Discord.createTitleOnly("Traitement de la commande...");

        if (interaction != null) {
            interaction.deferReply().addEmbeds(embed).flatMap(v -> v.retrieveOriginal().map(w -> {
                answerId = w.getId();
                answerTimeCreated = w.getTimeCreated();
                return this;
            })).queue();
        } else {
            message.replyEmbeds(embed).map(v -> {
                answerId = v.getId();
                answerTimeCreated = v.getTimeCreated();
                return this;
            }).queue();
        }

        int i = 0;

        while (answerId == null && i < 100) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }

            i++;
        }

        if (answerId == null) {
            Out.println("Couldn't send embed.");
        }
    }

    private void handleEmbed(MessageEmbed embed) {
        this.answerMessage = embed.getDescription();

        messageChannel.retrieveMessageById(answerId).map(v -> {
            if (!v.getContentRaw().equals("")) {
                v.editMessage(" ").queue();
            }

            v.editMessageEmbeds(embed).queue();

            return this;
        }).queue();
    }
}
