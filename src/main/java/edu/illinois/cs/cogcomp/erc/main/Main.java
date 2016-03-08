package edu.illinois.cs.cogcomp.erc.main;


import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.ir.DocUtils;
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

        Corpus ace05 = Utils.readSerializedCorpus(Parameters.ACE05_SERIALIZED_CORPUS);
        Document doc = ace05.getDoc(200);
        TextAnnotation ta = doc.getTA();
        System.out.println(ta.getAvailableViews());

        View ner =  ta.getView(Corpus.NER_GOLD_COARSE_VIEW);
        TokenLabelView token = (TokenLabelView) ta.getView(Corpus.TOKENS_VIEW);
        Utils.printTAConstitutents(ner.getConstituents());


        System.out.println("\n " + doc.getFilename());

        DocUtils.addNERBIOView(doc);
        View ner_bio = doc.getTA().getView(Corpus.NER_GOLD_BIO_VIEW);
        Utils.printTAConstitutents(ner_bio.getConstituents());







    }

    /**
               To read corpus from directory and write serialized docs
     //        Ace05Reader ace05reader = new Ace05Reader();
     //        List<Document> ace05docs = ace05reader.readCorpus();
     //        Corpus ace05 = new Corpus(ace05docs, ace05reader.checkis2004());
     //
     //        System.out.println("Number of ACE05 documents read - " + ace05docs.size());
     //
     //        System.out.println("Ace05 Stats : ");
     //        Utils.countCorpusTypeDocs(ace05);
     */




}
