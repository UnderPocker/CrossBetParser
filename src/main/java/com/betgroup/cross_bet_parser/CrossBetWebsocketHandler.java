package com.betgroup.cross_bet_parser;

import com.betgroup.cross_bet_parser.dao.MatchLocalDao;
import com.betgroup.cross_bet_parser.match.Match;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class CrossBetWebsocketHandler extends WebSocketClient {
    private final MatchLocalDao matchLocalDao;


    public CrossBetWebsocketHandler(URI serverUri, MatchLocalDao matchLocalDao) {
        super(serverUri);
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
