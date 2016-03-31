package edu.illinois.cs.cogcomp.erc.main;

import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusType;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusUtils;
import edu.illinois.cs.cogcomp.erc.sl.ner.MainClass;
import edu.illinois.cs.cogcomp.erc.sl.ner.NERExperiment;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.List;

/**
 * Created by nitishgupta on 2/19/16.
 */
public class Main {

    public static void main(String [] args) {
        ConfigSystem.initialize();

        List<Corpus> corpora  = CorpusUtils.readCompleteTrainDevTestCorpora(CorpusType.ACE05);
        Corpus ace05 = corpora.get(0);
        Corpus ace05train = corpora.get(1);
        Corpus ace05dev = corpora.get(2);
        Corpus ace05test = corpora.get(3);

        NERExperiment ner = new NERExperiment();
        ner.setCorpus(ace05);
        ner.runExperiment();

        corpora  = CorpusUtils.readCompleteTrainDevTestCorpora(CorpusType.ACE04);
        Corpus ace04 = corpora.get(0);
        Corpus ace04train = corpora.get(1);
        Corpus ace04dev = corpora.get(2);
        Corpus ace04test = corpora.get(3);

        ner = new NERExperiment();
        ner.setCorpus(ace04);
        ner.runExperiment();
    }
}
