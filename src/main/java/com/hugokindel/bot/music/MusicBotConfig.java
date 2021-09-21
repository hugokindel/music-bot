package com.hugokindel.bot.music;

import com.hugokindel.common.json.annotation.JsonSerializable;

import java.util.ArrayList;import java.util.List;

@JsonSerializable
public class MusicBotConfig {
    @JsonSerializable(necessary = false)
    public String hostId = "";

    @JsonSerializable(necessary = false)
    public String hostToken = "";

    @JsonSerializable(necessary = false)
    public List<String> workerIds = new ArrayList<>();

    @JsonSerializable(necessary = false)
    public List<String> workerTokens = new ArrayList<>();

    @JsonSerializable(necessary = false)
    public String guildId = "";

    @JsonSerializable(necessary = false)
    public String creatorId = "";

    @JsonSerializable(necessary = false)
    public String spotifyId = "";

    @JsonSerializable(necessary = false)
    public String spotifySecret = "";
}
