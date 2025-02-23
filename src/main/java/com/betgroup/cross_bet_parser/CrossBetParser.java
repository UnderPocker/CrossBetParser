package com.betgroup.cross_bet_parser;

import com.betgroup.cross_bet_parser.match.Game;
import com.betgroup.cross_bet_parser.match.Match;
import com.betgroup.cross_bet_parser.match_entities.MatchEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrossBetParser {
    public static Set<Match> parseHtml(String html) throws IOException, ParseException {
        Set<Match> matches = new HashSet<>();

        String regex = ".+var live = (.+);.+";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            String jsonString = matcher.group(1);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);

            MatchEntity[] matchEntities = mapper.readValue(new StringReader(jsonString), MatchEntity[].class);

            for (MatchEntity matchEntity : matchEntities) {
                Match match = getMatch(matchEntity);

                matches.add(match);
            }
        }

        return matches;
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

    private static Game parseGame(String s){
        return switch (s) {
            case "csgo" -> Game.CSGO;
            case "dota2" -> Game.DOTA2;
            default -> Game.UNKNOWN;
        };
    }
}
