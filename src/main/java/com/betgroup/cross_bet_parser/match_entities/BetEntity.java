package com.betgroup.cross_bet_parser.match_entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BetEntity {
    private String id;
    @JsonProperty("odds_home")
    private String oddsHome;
    @JsonProperty("odds_away")
    private String oddsAway;
}
