package edu.illinois.cs.cogcomp.erc.main;


import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.reader.Ace04Reader;
import edu.illinois.cs.cogcomp.erc.reader.Ace05Reader;
import edu.illinois.cs.cogcomp.erc.reader.DocumentReader;
import edu.illinois.cs.cogcomp.erc.util.Utils;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Created by nitishgupta on 2/19/16.
 */
public class Main {

    public static void main(String [] args) {
        ConfigSystem.initialize();

//        Ace04Reader ace04reader = new Ace04Reader();
//        List<Document> ace04docs = ace04reader.readCorpus();
//        Corpus ace04 = new Corpus(ace04docs, ace04reader.checkis2004());
//
//        System.out.println("Number of ACE04 documents read - " + ace04docs.size());
//
//        Ace05Reader ace05reader = new Ace05Reader();
//        List<Document> ace05docs = ace05reader.readCorpus();
//        Corpus ace05 = new Corpus(ace05docs, ace05reader.checkis2004());
//
//        System.out.println("Number of ACE05 documents read - " + ace05docs.size());
//
//        System.out.println("Ace04 Stats : ");
//        Utils.countCorpusTypeDocs(ace04);
//
//        System.out.println("Ace05 Stats : ");
//        Utils.countCorpusTypeDocs(ace05);

        Ace04Reader.readDocumentTester(Parameters.ACE04_DATA_DIR + "bn/", Parameters.ACE04_DATA_DIR + "bn/" + "ABC20001103.1830.1134.apf.xml");



    }
}
