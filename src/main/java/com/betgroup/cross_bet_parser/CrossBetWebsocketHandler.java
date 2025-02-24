package com.betgroup.cross_bet_parser;

import com.betgroup.cross_bet_parser.dao.MatchLocalDao;
import com.betgroup.cross_bet_parser.match.Match;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;

public class CrossBetWebsocketHandler extends WebSocketClient {
    private final MatchLocalDao matchLocalDao;


    public CrossBetWebsocketHandler(URI serverUri, Map<String, String> headers, MatchLocalDao matchLocalDao) {
        super(serverUri, headers);
        this.matchLocalDao = matchLocalDao;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send("42[\"room\",\"main\"]");

        for (String id : matchLocalDao.getAllMatches().keySet()) {
            send("42[\"room\",\"" + id + "\"]");
        }
    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);

        if (s.startsWith("3") || s.startsWith("40") || s.startsWith("0")) {
            return;
        }

        String json = s.replaceAll("42", "");
        JSONArray jsonArray = (JSONArray) JSONValue.parse(json);

        String opType = jsonArray.get(0).toString();
        JSONObject details = (JSONObject) jsonArray.get(1);
        String matchId = null;
        Match match = null;

        if (!opType.equals("matchesList")) {
            matchId = details.get("matchId").toString();
            match = matchLocalDao.get(matchId);
        }

        switch (opType){
            case "scoreUpdate":
                int score1 = Integer.parseInt(details.get("roundScore_home").toString());
                int score2 = Integer.parseInt(details.get("roundScore_away").toString());

                match.setScore1(score1);
                match.setScore2(score2);
                break;
            case "mapScore":
                int mapScore1 = Integer.parseInt(details.get("mapScore_home").toString());
                int mapScore2 = Integer.parseInt(details.get("mapScore_away").toString());
                int mapNum = Integer.parseInt(details.get("mapNum").toString());

                match.setMapScore1(mapScore1);
                match.setMapScore2(mapScore2);
                match.setMapNum(mapNum);
                break;
            case "utilsUpdate":
                String type = details.get("type").toString();
                if (type.equals("map")){
                    String map = details.get("map").toString();
                    match.setMap(map);
                }
                break;
            case "matchClose":
                matchLocalDao.delete(matchId);
                break;
            case "matchesList":
                String live = details.get("live").toString();
                try {
                    Set<Match> matchSet = CrossBetParser.parseMatchEntities(live);

                    for (Match liveMatch : matchSet) {
                        if (!matchLocalDao.contains(liveMatch.getId())) {
                            String matchHtml = CrossBetRequestHandler.getMatchHtmlString(liveMatch.getId());
                            CrossBetParser.parseMatchDetails(matchHtml, liveMatch);
                            send("42[\"room\",\"" + liveMatch.getId() + "\"]");
                            matchLocalDao.save(liveMatch);
                        }
                    }

                } catch (IOException | ParseException | InterruptedException e) {
                    ExceptionHandler.log(e);
                }

        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        ExceptionHandler.log(e);
        System.exit(0);
    }
}
