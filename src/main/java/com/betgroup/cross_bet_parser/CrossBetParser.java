package com.betgroup.cross_bet_parser;

import com.betgroup.cross_bet_parser.match.Bet;
import com.betgroup.cross_bet_parser.match.Game;
import com.betgroup.cross_bet_parser.match.Match;
import com.betgroup.cross_bet_parser.match_entities.BetEntity;
import com.betgroup.cross_bet_parser.match_entities.MatchEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrossBetParser {
    public static Set<Match> parseHtml(String html) throws IOException, ParseException {
        String regex = ".+var live = (.+);.*?var upcoming.*?";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            String jsonString = matcher.group(1);
            return parseMatchEntities(jsonString);
        }

        return Collections.emptySet();
    }

    public static Set<Match> parseMatchEntities(String json) throws IOException, ParseException {
        Set<Match> matches = new HashSet<>();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);

        MatchEntity[] matchEntities = mapper.readValue(new StringReader(json), MatchEntity[].class);

        for (MatchEntity matchEntity : matchEntities) {
            Match match = getMatch(matchEntity);

            matches.add(match);
        }

        return matches;
    }

    public static void parseMatchDetails(String matchHtml, Match match) throws IOException {
        String regex = ".+var match = (.+);.*?var predictions.*?";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(matchHtml);

        if (matcher.find()) {
            String jsonString = matcher.group(1);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);

            MatchEntity matchEntity = mapper.readValue(new StringReader(jsonString), MatchEntity.class);

            match.setScore1(matchEntity.getRoundScoreHome());
            match.setScore2(matchEntity.getRoundScoreAway());
            match.setMapScore1(matchEntity.getMapScoreHome());
            match.setMapScore2(matchEntity.getMapScoreAway());
            match.setMap(matchEntity.getMap());
            match.setMapNum(matchEntity.getMapNum());
            match.setBets(getBets(matchEntity.getCross()));
        }
    }

    private static Match getMatch(MatchEntity matchEntity) throws ParseException {
        Match match = new Match();
        match.setId(matchEntity.getMatchId());
        match.setGame(parseGame(matchEntity.getGame()));
        match.setTeam1(matchEntity.getTeams()[0].getName());
        match.setTeam2(matchEntity.getTeams()[1].getName());

        String timeStr = matchEntity.getDate() + " " + matchEntity.getTime();
        timeStr = timeStr.replaceAll("st|nd|rd|th| of", "");
        SimpleDateFormat fromStr = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.ENGLISH);
        Date time = fromStr.parse(timeStr);
        match.setTime(time);
        return match;
    }

    private static Map<String, Bet> getBets(Map<String, BetEntity> betEntities) {
        Map<String, Bet> bets = new HashMap<>();
        for (Map.Entry<String, BetEntity> entry : betEntities.entrySet()) {
            BetEntity betEntity = entry.getValue();
            Bet bet = new Bet();
            if (betEntity.getOddsAway() != null && betEntity.getOddsHome() != null && !betEntity.getOddsAway().equals("-") && !betEntity.getOddsHome().equals("-")) {
                bet = new Bet(Double.parseDouble(betEntity.getOddsHome()), Double.parseDouble(betEntity.getOddsAway()));
            }
            bets.put(entry.getKey(), bet);
        }

        return bets;
    }

    private static Game parseGame(String s){
        return switch (s) {
            case "csgo" -> Game.CSGO;
            case "dota2" -> Game.DOTA2;
            default -> Game.UNKNOWN;
        };
    }
}
