package com.stellifai.main;

import org.slf4j.LoggerFactory;
import org.stellifai.credentials.Anoncreds;
import org.stellifai.credentials.Ledger;
import java.util.logging.Logger;

public class Main {
    private final static Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws Exception{
        //System.out.println("Hello Main App");
        logger.info("Hello Main App");
        //Anoncreds.demo();
        Ledger.initialize();
    }
}
