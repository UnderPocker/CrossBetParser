package com.betgroup.cross_bet_parser.dao;

import com.betgroup.cross_bet_parser.match.Match;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchLocalDaoImpl implements MatchLocalDao{
    private final Map<String, Match> matches = new ConcurrentHashMap<>();
    @Override
    public void save(Match match) {
        matches.put(match.getId(), match);
    }

    @Override
    public Match delete(String id) {
        return matches.remove(id);
    }

    @Override
    public Match get(String id) {
        return matches.get(id);
    }

    @Override
    public boolean contains(String id) {
        return matches.containsKey(id);
    }

    @Override
    public Map<String, Match> getAllMatches() {
        return matches;
    }

    @Override
    public boolean isDBEmpty() {
        return matches.isEmpty();
    }

    @Override
    public void clearMatches() {
        matches.clear();
    }
}
