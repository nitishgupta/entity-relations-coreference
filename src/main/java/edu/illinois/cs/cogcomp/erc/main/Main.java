package edu.illinois.cs.cogcomp.erc.main;


import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.reader.Ace04Reader;
import edu.illinois.cs.cogcomp.erc.reader.Ace05Reader;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;

import java.util.List;

/**
 * Created by nitishgupta on 2/19/16.
 */
public class Main {

    public static void main(String [] args) {
        ConfigSystem.initialize();

//        Ace04Reader ace04 = new Ace04Reader();
//        List<Document> ace04Corpus = ace04.readCorpus();
//
//        System.out.println("Number of ACE04 documents read - " + ace04Corpus.size());

        Ace05Reader ace05 = new Ace05Reader();
//        List<Document> ace05Corpus = ace05.readCorpus();

//        System.out.println("Number of ACE05 documents read - " + ace05Corpus.size());

        String fileName = "bn/CNN_ENG_20030506_160524.18.apf.xml";
        ace05.readDocument(fileName);

    }
}
