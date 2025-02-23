package com.betgroup.cross_bet_parser.match;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.*;

@Data
public class Match implements Cloneable{
    private String id;
    private Date time;
    private String team1, team2;
    private Game game;
    private boolean wasNotified;
    private int score1, score2;


    @Override
    public String toString() {
        SimpleDateFormat toStr = new SimpleDateFormat("HH:mm d MMM");
        return String.format("%s %s \"%s vs %s\"", toStr.format(time), game, team1, team2);
    }

    @Override
    protected Match clone() throws CloneNotSupportedException {
        Match cloned = (Match) super.clone(); // Поверхностное клонирование

        // Глубокое копирование изменяемых объектов
        cloned.time = (time != null) ? (Date) time.clone() : null;


        return cloned;
    }
}
