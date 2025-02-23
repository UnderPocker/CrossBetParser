package com.betgroup.cross_bet_parser.match_entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

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

}
