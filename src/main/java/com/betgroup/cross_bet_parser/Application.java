package com.betgroup.cross_bet_parser;

import com.betgroup.cross_bet_parser.dao.MatchLocalDao;
import com.betgroup.cross_bet_parser.dao.MatchLocalDaoImpl;
import com.betgroup.cross_bet_parser.match.Match;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
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
            matchLocalDao.save(match);
        }
    }

}
