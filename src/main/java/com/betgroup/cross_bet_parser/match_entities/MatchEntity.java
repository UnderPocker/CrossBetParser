package com.betgroup.cross_bet_parser.match_entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchEntity {
    private String event;
    private String bestof;
    private String game;
    private String matchId;
    private Team[] teams;
    private String date;
    private String time;
    @JsonProperty("mapScore_home")
    private int mapScoreHome;
    @JsonProperty("mapScore_away")
    private int mapScoreAway;
    @JsonProperty("roundScore_home")
    private int roundScoreHome;
    @JsonProperty("roundScore_away")
    private int roundScoreAway;
    private String map;
    private int mapNum;

    private Map<String, BetEntity> cross;
}
