package com.betgroup.cross_bet_parser;

import com.betgroup.cross_bet_parser.dao.MatchLocalDao;
import com.betgroup.cross_bet_parser.dao.MatchLocalDaoImpl;
import com.betgroup.cross_bet_parser.match.Match;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Application {
    private final MatchLocalDao matchLocalDao = new MatchLocalDaoImpl();
    public static void main(String[] args) {
        Application app = new Application();
        try {
            app.parse();
        } catch (IOException | URISyntaxException | InterruptedException | ParseException e) {
            ExceptionHandler.log(e);
            System.exit(0);
        }
    }

    public void parse() throws IOException, URISyntaxException, InterruptedException, ParseException {
        String html = CrossBetRequestHandler.getHtmlString();
        Set<Match> matches = CrossBetParser.parseHtml(html);

        for (Match match : matches) {
            String matchHtml = CrossBetRequestHandler.getMatchHtmlString(match.getId());
            CrossBetParser.parseMatchDetails(matchHtml, match);
            matchLocalDao.save(match);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");

        CrossBetWebsocketHandler websocketHandler =
                new CrossBetWebsocketHandler(new URI("wss://cross.bet/socket.io/?EIO=3&transport=websocket"), headers, matchLocalDao);
        websocketHandler.connectBlocking();

        while (!websocketHandler.isClosed()) {
            websocketHandler.send("2");
            Thread.sleep(25000);

            if (websocketHandler.isClosed()){
                websocketHandler = new CrossBetWebsocketHandler(new URI("wss://cross.bet/socket.io/?EIO=3&transport=websocket"), headers, matchLocalDao);
                websocketHandler.connectBlocking();
            }
        }
    }

}
