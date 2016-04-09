package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

/**
 * Created by nitishgupta on 3/30/16.
 */
public class NERExperiment {

    Corpus corpus;

    public NERExperiment(){
        corpus = null;
    }

    public void setCorpus(Corpus corpus){
        this.corpus = corpus;
    }

    public void runExperiment(){
        if(corpus == null){
            System.err.println("Corpus not set in Experiment");
            return;
        }

        Lexiconer lm = new Lexiconer();
        lm.setAllowNewFeatures(true);
        SLProblem sp = MainClass.readStructuredData(corpus, lm, Corpus.NER_GOLD_HEAD_BIO_VIEW);
    }
}
