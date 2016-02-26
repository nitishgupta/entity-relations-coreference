package edu.illinois.cs.cogcomp.main;


import edu.illinois.cs.cogcomp.config.ConfigSystem;
import edu.illinois.cs.cogcomp.config.Parameters;
import edu.illinois.cs.cogcomp.reader.Ace04Reader;
import edu.illinois.cs.cogcomp.reader.Ace05Reader;

/**
 * Created by nitishgupta on 2/19/16.
 */
public class Main {

    public static void main(String [] args) {
        ConfigSystem.initialize();

        new Ace05Reader().testProcessDocument(Ace05Reader.TEST_DIR, Ace05Reader.TEST_FILE);
        new Ace04Reader().testProcessDocument(Ace04Reader.TEST_DIR, Ace04Reader.TEST_FILE);
    }
}
