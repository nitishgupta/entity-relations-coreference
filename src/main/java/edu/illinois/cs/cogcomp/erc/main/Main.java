package edu.illinois.cs.cogcomp.erc.main;


import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.reader.Ace04Reader;
import edu.illinois.cs.cogcomp.erc.reader.Ace05Reader;

/**
 * Created by nitishgupta on 2/19/16.
 */
public class Main {

    public static void main(String [] args) {
        ConfigSystem.initialize();


        //Ace05Reader ace05 = new Ace05Reader();
        //ace05.readCorpus_and_WriteSerialized();

        Ace04Reader ace04 = new Ace04Reader();
        ace04.readCorpus_and_WriteSerialized();

//        System.out.print("Asd");



    }
}
