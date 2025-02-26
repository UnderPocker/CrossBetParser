package com.betgroup.cross_bet_parser.match;

import lombok.Data;

import java.util.*;

@Data
public class Match implements Cloneable{
    private String id;
    private Date time;
    private String team1, team2;
    private Game game;
    private boolean wasNotified;
    private int score1, score2;
    private int mapScore1, mapScore2;
    private int mapNum;
    private String map;
    private Map<String, Bet> bets = new HashMap<>();

    @Override
    protected Match clone() throws CloneNotSupportedException {
        Match cloned = (Match) super.clone(); // Поверхностное клонирование

        // Глубокое копирование изменяемых объектов
        cloned.time = (time != null) ? (Date) time.clone() : null;


        return cloned;
    }
}
