package edu.illinois.cs.cogcomp.erc.corpus;

import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.DocUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.reader.Ace04Reader;
import edu.illinois.cs.cogcomp.erc.reader.Ace05Reader;
import edu.illinois.cs.cogcomp.erc.reader.DocumentReader;
import edu.illinois.cs.cogcomp.erc.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitishgupta on 3/9/16.
 */
public class CorpusUtils {
    public static Corpus readCorpus(CorpusType corpusType){
        Corpus corpus;
        boolean is2004 = (corpusType == CorpusType.ACE04);
        String serializedCorpusFile = is2004 ? Parameters.ACE04_SERIALIZED_CORPUS : Parameters.ACE05_SERIALIZED_CORPUS;

        if ((new File(serializedCorpusFile)).exists()) {
            corpus = Utils.readSerializedCorpus(serializedCorpusFile);
        } else {
            // To read corpus from directory and write serialized docs
            DocumentReader documentReader = is2004 ? new Ace04Reader() : new Ace05Reader();
            List<Document> corpusDocuments = documentReader.readCorpus();
            corpus = new Corpus(corpusDocuments, is2004);

            edu.illinois.cs.cogcomp.erc.util.Utils.writeSerializedCorpus(corpus, serializedCorpusFile);
        }

        if (corpus != null) {
            System.out.println("Reading documents from " + (is2004 ? "ACE2004" : "ACE2005"));
            System.out.println("Number of documents read - " + corpus.getDocs().size());
            System.out.println("Corpus Stats : ");
            Utils.countCorpusTypeDocs(corpus);

            for(Document doc : corpus.getDocs()) {
                DocUtils.addNERCoarseBIOView(doc);
            }
        }

        return corpus;
    }

    public static List<Corpus> getTrainDevTestCorpora(Corpus corpus){
        List<Document> alldocs = corpus.getDocs();

        int start_train = 0, end_train = (int) (Parameters.train_perc * alldocs.size());
        int start_dev = end_train, end_dev = (int) ((Parameters.train_perc + Parameters.dev_perc) * alldocs.size());
        int start_test = end_dev, end_test = alldocs.size();

        List<Document> train = alldocs.subList(start_train, end_train);
        List<Document> dev = alldocs.subList(start_dev, end_dev);
        List<Document> test = alldocs.subList(start_test, end_test);

        Corpus tr = new Corpus(train, corpus.checkisACE2004());
        Corpus te = new Corpus(test, corpus.checkisACE2004());
        Corpus deve = new Corpus(dev, corpus.checkisACE2004());

        System.out.println("Number of files after split : ");
        System.out.println("Train : " + train.size());
        System.out.println("Dev : " + dev.size());
        System.out.println("Test : " + test.size());

        List<Corpus> corpora = new ArrayList<Corpus>();
        corpora.add(tr);
        corpora.add(deve);
        corpora.add(te);

        return corpora;
    }

    public static List<Corpus> readCompleteTrainDevTestCorpora(CorpusType corpusType){
        Corpus corpus = CorpusUtils.readCorpus(corpusType);
        List<Corpus> corpora = CorpusUtils.getTrainDevTestCorpora(corpus);
        corpora.add(0, corpus);
        return corpora;
    }
}
