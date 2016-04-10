package edu.illinois.cs.cogcomp.erc.corpus;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.DocUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.util.Utils;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitishgupta on 3/9/16.
 */
public class CorpusUtils {
    public static Corpus readCorpus(CorpusType corpusType) throws Exception {
        Corpus corpus;
        boolean is2004 = (corpusType == CorpusType.ACE04);
        String serializedCorpusFile = is2004 ? Parameters.ACE04_SERIALIZED_CORPUS : Parameters.ACE05_SERIALIZED_CORPUS;

        if ((new File(serializedCorpusFile)).exists()) {
            System.out.println("***  READING SERIALIZED CORPUS *** ");
            corpus = Utils.readSerializedCorpus(serializedCorpusFile);
        } else {
            // To read corpus from directory and write serialized docs
            System.out.println("***  READING DOCS DIRECTLY  *** ");

            String datasetPath = is2004 ? Parameters.ACE04_DATA_DIR : Parameters.ACE05_DATA_DIR;
            ACEReader aceDocumentReader = new ACEReader(datasetPath, is2004);

            List<Document> corpusDocuments = new ArrayList<>();
            for (TextAnnotation ta : aceDocumentReader) {
                Document doc = new Document(ta, is2004, ta.getId());

                // ADDING SPAN NER EXTENT VIEW IN TA
                if(!doc.getTA().hasView(Corpus.NER_GOLD_EXTENT_SPAN))
                    DocUtils.createGOLDNER_ExtentView(doc);

                if(!doc.getTA().hasView(Corpus.NER_GOLD_HEAD_SPAN))
                    DocUtils.createGOLDNER_HeadView(doc);

                // ADDING BIO NER EXTENT VIEW IN TA
                if(!doc.getTA().hasView(Corpus.NER_GOLD_HEAD_BIO_VIEW))
                    DocUtils.addNERHeadBIOView(doc);

                if(!doc.getTA().hasView(Corpus.NER_GOLD_EXTENT_BIO_VIEW))
                    DocUtils.addNERExtentBIOView(doc);

                corpusDocuments.add(doc);
            }
            corpus = new Corpus(corpusDocuments, is2004);

            Utils.writeSerializedCorpus(corpus, serializedCorpusFile);
        }

        if (corpus != null) {
            System.out.println("Reading documents from " + (is2004 ? "ACE2004" : "ACE2005"));
            System.out.println("Number of documents read - " + corpus.getDocs().size());
            System.out.println("Corpus Stats : ");
            Utils.countCorpusTypeDocs(corpus);

            /* TODO : STOPPING ADDING BIO VIEW UNTIL NER_GOLD_VIEW IS ESTABLISHED */
//            for(Document doc : corpus.getDocs()) {
//                DocUtils.addNERCoarseBIOView(doc);
//            }
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

    public static List<Corpus> readCompleteTrainDevTestCorpora(CorpusType corpusType) throws Exception {
        Corpus corpus = CorpusUtils.readCorpus(corpusType);
        List<Corpus> corpora = CorpusUtils.getTrainDevTestCorpora(corpus);
        corpora.add(0, corpus);
        return corpora;
    }
}
