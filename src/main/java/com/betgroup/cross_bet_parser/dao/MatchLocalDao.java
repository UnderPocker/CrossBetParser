package com.betgroup.cross_bet_parser.dao;



import com.betgroup.cross_bet_parser.match.Match;

import java.util.Map;

public interface MatchLocalDao {
    void save(Match match);
    Match delete(String id);
    Match get(String id);
    boolean contains(String id);
    Map<String, Match> getAllMatches();
    boolean isDBEmpty();
    void clearMatches();
}
