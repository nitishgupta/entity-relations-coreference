package edu.illinois.cs.cogcomp.erc.main;

import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusType;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.sl.ner.annotators.NERAnnotator;
import edu.illinois.cs.cogcomp.openeval.learner.Server;
import edu.illinois.cs.cogcomp.openeval.learner.ServerPreferences;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import fi.iki.elonen.util.ServerRunner;

import java.util.List;

/**
 * Created by nitishgupta on 2/19/16.
 */

public class TestUtilities {



    public static void main(String [] args) throws Exception {
        ConfigSystem.initialize();

        List<Corpus> corpora  = CorpusUtils.readCompleteTrainDevTestCorpora(CorpusType.ACE05);
        Corpus ace05 = corpora.get(0);
        Corpus ace05train = corpora.get(1);
        Corpus ace05dev = corpora.get(2);
        Corpus ace05test = corpora.get(3);

        //System.out.println(ace05.getDocs().get(5).getTA().getAvailableViews());

        Document doc = ace05.getDocs().get(5);
        TextAnnotation ta = doc.getTA();
//        View view = ta.getView(Corpus.NER_GOLD_HEAD_SPAN);
//
//        List<Constituent> constituents = view.getConstituents();
//        for(Constituent c : constituents){
//            System.out.println(c.getSurfaceForm() + "_" + c.getLabel() + " " + c.getStartSpan());
//        }
//        System.out.println();
//

        SLModel model = SLModel.loadModel("testModel");
        Annotator annotator = new NERAnnotator(model, false, Corpus.NER_PRED_HEAD_BIO_VIEW, "ENTITYVIEW");
        Server client = new Server(5757, new ServerPreferences(10000, 1), annotator);

        ServerRunner.executeInstance(client);


//        NERExperiment ner = new NERExperiment();
//        ner.setCorpus(ace05);
//        ner.runExperiment();

//        corpora  = CorpusUtils.readCompleteTrainDevTestCorpora(CorpusType.ACE04);
//        Corpus ace04 = corpora.get(0);
//        Corpus ace04train = corpora.get(1);
//        Corpus ace04dev = corpora.get(2);
//        Corpus ace04test = corpora.get(3);
//
//        ner = new NERExperiment();
//        ner.setCorpus(ace04);
//        ner.runExperiment();
    }
}
