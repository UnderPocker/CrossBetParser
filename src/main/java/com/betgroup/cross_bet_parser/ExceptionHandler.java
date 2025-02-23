package com.betgroup.cross_bet_parser;

import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ExceptionHandler {
    private static final Logger logger;
    static {
        logger = Logger.getLogger("ErrorLogger");
        FileHandler fh;

        try {
            // настроить файл для записи логов
            fh = new FileHandler("error.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public static void log(Throwable e){
        logger.severe(e.getMessage() + "\n" + e.getClass() + "\n" + Arrays.toString(e.getStackTrace()));
    }
}
