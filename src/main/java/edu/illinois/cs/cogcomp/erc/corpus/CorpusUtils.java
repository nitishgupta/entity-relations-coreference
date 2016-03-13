package edu.illinois.cs.cogcomp.erc.corpus;

import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.DocUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.reader.Ace04Reader;
import edu.illinois.cs.cogcomp.erc.reader.Ace05Reader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nitishgupta on 3/9/16.
 */
public class CorpusUtils {

    public static Corpus readACE05Corpus(){
        Corpus ace05;
        if ((new File(Parameters.ACE05_SERIALIZED_CORPUS)).exists()) {
            ace05 = edu.illinois.cs.cogcomp.erc.util.Utils.readSerializedCorpus(Parameters.ACE05_SERIALIZED_CORPUS);
        } else {
            // To read corpus from directory and write serialized docs
            Ace05Reader ace05reader = new Ace05Reader();
            List<Document> ace05docs = ace05reader.readCorpus();
            ace05 = new Corpus(ace05docs, ace05reader.checkis2004());


            edu.illinois.cs.cogcomp.erc.util.Utils.writeSerializedCorpus(ace05, Parameters.ACE05_SERIALIZED_CORPUS);
        }

        if(ace05 != null) {
            System.out.println("Number of ACE05 documents read - " + ace05.getDocs().size());
            System.out.println("Ace05 Stats : ");
            edu.illinois.cs.cogcomp.erc.util.Utils.countCorpusTypeDocs(ace05);
            for(Document doc : ace05.getDocs()){
                DocUtils.addNERCoarseBIOView(doc);
            }
        }

        return ace05;
    }

    public static Corpus readACE04Corpus(){
        Corpus ace04;
        if ((new File(Parameters.ACE04_SERIALIZED_CORPUS)).exists()) {
            ace04 = edu.illinois.cs.cogcomp.erc.util.Utils.readSerializedCorpus(Parameters.ACE04_SERIALIZED_CORPUS);
        } else {
            // To read corpus from directory and write serialized docs
            Ace04Reader ace04reader = new Ace04Reader();
            List<Document> ace04docs = ace04reader.readCorpus();
            ace04 = new Corpus(ace04docs, ace04reader.checkis2004());

            System.out.println("Number of ACE05 documents read - " + ace04docs.size());

            System.out.println("Ace04 Stats : ");
            edu.illinois.cs.cogcomp.erc.util.Utils.countCorpusTypeDocs(ace04);
            edu.illinois.cs.cogcomp.erc.util.Utils.writeSerializedCorpus(ace04, Parameters.ACE04_SERIALIZED_CORPUS);
        }

        return ace04;
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

    public static List<Corpus> readACE05CompleteTrainDevTestCorpora(){
        Corpus ace05 = CorpusUtils.readACE05Corpus();
        List<Corpus> corpora = CorpusUtils.getTrainDevTestCorpora(ace05);
        corpora.add(0, ace05);
        return corpora;
    }

}
