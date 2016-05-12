package edu.illinois.cs.cogcomp.erc.corpus;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.ir.DocUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.util.Utils;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nitishgupta on 3/9/16.
 */
public class CorpusUtils {
    private static String trainFileList = "data/open-eval-train-files.txt";
    private static Logger logger = LoggerFactory.getLogger(CorpusUtils.class);

    /**
     *
     * @param corpusType : ACE04 or ACE05
     * @return  Corpus object with gold views and NER GOLD BIO (head and extent)
     * @throws Exception
     */
    public static Corpus readCorpus(CorpusType corpusType) throws Exception {
        Corpus corpus;
        boolean is2004 = (corpusType == CorpusType.ACE04);
        String serializedCorpusFile = is2004 ? Parameters.ACE04_SERIALIZED_CORPUS : Parameters.ACE05_SERIALIZED_CORPUS;

        if ((new File(serializedCorpusFile)).exists()) {
            logger.info("***  READING SERIALIZED CORPUS *** ");
            corpus = Utils.readSerializedCorpus(serializedCorpusFile);
        } else {
            // To read corpus from directory and write serialized docs
            logger.info("***  READING DOCS DIRECTLY  *** ");

            String datasetPath = is2004 ? Parameters.ACE04_DATA_DIR : Parameters.ACE05_DATA_DIR;
            String sectionsToRead = is2004 ? Parameters.ACE04_SECTION_LIST : Parameters.ACE05_SECTION_LIST;
            String[] sections = sectionsToRead.split(",");

            // Quirk in the reader.
            if (sections.length == 0) sections = null;
            ACEReader aceDocumentReader = new ACEReader(datasetPath, sections, is2004);

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
            logger.info("Reading documents from " + (is2004 ? "ACE2004" : "ACE2005"));
            logger.info("Number of documents read - " + corpus.getDocs().size());
            logger.info("Corpus Stats : ");
            Utils.countCorpusTypeDocs(corpus);
        }

        return corpus;
    }

//    public static List<Corpus> getTrainDevTestCorpora(Corpus corpus){
//        List<Document> alldocs = corpus.getDocs();
//
//        int start_train = 0, end_train = (int) (Parameters.train_perc * alldocs.size());
//        int start_dev = end_train, end_dev = (int) ((Parameters.train_perc + Parameters.dev_perc) * alldocs.size());
//        int start_test = end_dev, end_test = alldocs.size();
//
//        List<Document> train = alldocs.subList(start_train, end_train);
//        List<Document> dev = alldocs.subList(start_dev, end_dev);
//        List<Document> test = alldocs.subList(start_test, end_test);
//
//        Corpus tr = new Corpus(train, corpus.checkisACE2004());
//        Corpus te = new Corpus(test, corpus.checkisACE2004());
//        Corpus deve = new Corpus(dev, corpus.checkisACE2004());
//
//        System.out.println("Number of files after split : ");
//        System.out.println("Train : " + train.size());
//        System.out.println("Dev : " + dev.size());
//        System.out.println("Test : " + test.size());
//
//        List<Corpus> corpora = new ArrayList<Corpus>();
//        corpora.add(tr);
//        corpora.add(deve);
//        corpora.add(te);
//
//        return corpora;
//    }

    public static List<Corpus> getTrainDevTestCorpora(Corpus corpus){
        List<Document> alldocs = corpus.getDocs();

        List<Document> train = new ArrayList<>();
        List<Document> dev = new ArrayList<>();
        List<Document> test = new ArrayList<>();

        try {
            String fileListContents = LineIO.slurp(trainFileList);
            String[] trainFileNames = fileListContents.split("\n");
            Set<String> trainFilesSet = new HashSet<>();

            for (String s : trainFileNames) {
                trainFilesSet.add(s);
            }

            for (Document doc : alldocs) {
                String match = null;

                for (String s : trainFilesSet) {
                    if (doc.getTA().getId().endsWith(s)) {
                        match = s;
                        break;
                    }
                }

                if (match != null) {
                    train.add(doc);
                    trainFilesSet.remove(match);
                } else {
                    test.add(doc);
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

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

    public static List<Corpus> readCompleteTrainDevTestCorpora(CorpusType corpusType) {
        Corpus corpus = null;
        try {
            corpus = CorpusUtils.readCorpus(corpusType);
        } catch(Exception e){
            e.printStackTrace();
        }
        List<Corpus> corpora = CorpusUtils.getTrainDevTestCorpora(corpus);
        corpora.add(0, corpus);
        return corpora;
    }

    public static CorpusType getCorpusTypeEnum(String corpusTypeString) {
        return Enum.valueOf(CorpusType.class, corpusTypeString.trim().toUpperCase());
    }
}
