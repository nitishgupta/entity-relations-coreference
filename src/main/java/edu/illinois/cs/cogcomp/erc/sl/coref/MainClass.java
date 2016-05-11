package edu.illinois.cs.cogcomp.erc.sl.coref;


import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.experiments.ClassificationTester;
import edu.illinois.cs.cogcomp.core.utilities.Table;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandIgnore;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusType;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.sl.coref.annotators.GoldMentionAnnotator;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.learner.l2_loss_svm.L2LossSSVMLearner;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nitishgupta on 3/30/16.
 */

public class MainClass {
    private static final String DefaultCorefModel = "DefaultCorefModel.model";
    private static final Logger logger = LoggerFactory.getLogger(MainClass.class);

    @CommandDescription(usage = "", description = "Read Corpus Only")
    public static Corpus readCorpus(String corpusType) {
        Corpus aceCorpus = null;
        try {
            CorpusType type = CorpusUtils.getCorpusTypeEnum(corpusType);
            aceCorpus = CorpusUtils.readCorpus(type);
        } catch (Exception ex) {
            logger.error("Error while parsing corpus type", ex);
        }
        return aceCorpus;
    }

    @CommandDescription(usage = "", description = "")
    public static void train(String corpusType) throws Exception {
        train(corpusType, DefaultCorefModel);
    }

    @CommandDescription(usage = "", description = "")
    public static void train(String corpusType, String modelFileName) throws Exception {
        Corpus aceCorpus = MainClass.readCorpus(corpusType);

        List<Corpus> allCorpora = CorpusUtils.getTrainDevTestCorpora(aceCorpus);
        Corpus trainCorpus = allCorpora.get(0);

        SLModel model = new SLModel();
        model.lm = new Lexiconer();
        model.lm.setAllowNewFeatures(true);

        FeatureGenerator fg = new FeatureGenerator(model.lm);
        model.featureGenerator = fg;

        SLProblem slProblem = new SLProblem();
        for (Document doc : trainCorpus.getDocs()) {
            List<Pair<CorefMentionPair, CorefLabel>> instances = SLHelper.populateSLProblemForDocument(
                    doc,
                    model.lm,
                    Parameters.COREF_MENTION_VIEW_GOLD,
                    Parameters.COREF_VIEW_GOLD);

            for (Pair<CorefMentionPair, CorefLabel> ins : instances) {
                fg.preExtractFeatures(ins.getFirst());
                slProblem.addExample(ins.getFirst(), ins.getSecond());
            }
        }

        // Extraction Done
        System.out.println("Number of Features : " + model.lm.getNumOfFeature());
        System.out.println("Number of Labels : " + model.lm.getNumOfLabels());

        // Disallow the creation of new features
        model.lm.setAllowNewFeatures(false);

        // initialize the inference solver
        model.infSolver = new InferenceSolver(model.lm, fg);
        SLParameters parameters = new SLParameters();

        parameters.loadConfigFile(Parameters.SL_PARAMETER_CONFIG_FILE);
        parameters.TOTAL_NUMBER_FEATURE = model.lm.getNumOfFeature();

        Learner learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, parameters);
        model.wv = learner.train(slProblem);

        WeightVector.printSparsity(model.wv);

        if(learner instanceof L2LossSSVMLearner)
            System.out.println("Primal objective:" +
                    ((L2LossSSVMLearner)learner).getPrimalObjective(
                            slProblem,
                            model.wv,
                            model.infSolver,
                            parameters.C_FOR_STRUCTURE));

        // save the model
        model.saveModel(modelFileName);
    }

    @CommandDescription(usage = "", description = "")
    public static void test(String corpusType) throws Exception {
        SLModel slModel = SLModel.loadModel(DefaultCorefModel);
        testModel(corpusType, slModel);
    }
//
    @CommandDescription(usage = "", description = "")
    public static void test(String corpusType, String modelFileName) throws Exception {
        SLModel slModel = SLModel.loadModel(modelFileName);
        testModel(corpusType, slModel);
    }
//
//    @CommandDescription(usage = "", description = "")
//    public static void trainAndTest(String corpusType) throws Exception { trainAndTest(corpusType, DefaultCorefModel); }
//
//    @CommandDescription(usage = "", description = "")
//    public static void trainAndTest(String corpusType, String modelFileName) throws Exception {
//        train(corpusType, modelFileName);
//        test(corpusType, modelFileName);
//    }

    @CommandIgnore
    public static void testModel(String corpusType, SLModel modelInstance) throws Exception {
        Corpus aceCorpus = MainClass.readCorpus(corpusType);

        List<Corpus> allCorpora = CorpusUtils.getTrainDevTestCorpora(aceCorpus);
        Corpus testCorpus = allCorpora.get(2);

        CorefEvaluator evaluator = new CorefEvaluator();

        ClassificationTester clfTester = new ClassificationTester();

        String corefGoldView = Parameters.COREF_VIEW_GOLD;
        String corefPredictedView = Parameters.COREF_VIEW_PREDICTION;

        // Annotator instance is used to create the predicted view in the textAnnotation.
        Annotator annotator = new GoldMentionAnnotator(
                corefPredictedView,                                                // Final View
                new String[] { ViewNames.POS, ViewNames.TOKENS, Parameters.COREF_MENTION_VIEW_GOLD},
                modelInstance,
                aceCorpus.checkisACE2004());

        System.out.println("ANNOTATOR IS MADE");

        // Annotate a TA and evaluate its performance.
        for (Document doc : testCorpus.getDocs()) {
            TextAnnotation ta = doc.getTA();
            annotator.addView(ta);

            //evaluator.evaluate(clfTester, ta.getView(corefGoldView), ta.getView(corefPredictedView));

        }

        // Print the performance table.
        Table performanceTable = clfTester.getPerformanceTable(true);
        System.out.println(performanceTable.toOrgTable());

        testTesting(testCorpus);
    }

    @CommandIgnore
    public static void main(String[] args) throws Exception {
        ConfigSystem.initialize();

        InteractiveShell<MainClass> shell = new InteractiveShell<>(MainClass.class);

        if (args.length == 0) {
            shell.showDocumentation();
        } else {
            shell.runCommand(args);
        }



        //test();
    }

    public static void test(){
        Corpus corpus = readCorpus("ACE05");
        Document doc = corpus.getDoc(5);
        TextAnnotation ta = doc.getTA();
        View corefView = ta.getView(Parameters.COREF_VIEW_GOLD);

        List<Constituent> constituents = corefView.getConstituents();
        //noinspection Since15
        constituents.sort(new Comparator<Constituent>(){
            @Override
            public int compare(Constituent o1, Constituent o2) {
                if(o1.getStartSpan() >= o2.getStartSpan() )
                    return 1;
                else
                    return -1;
            }
        } );

        for(Constituent c : constituents){
            System.out.println(c.getSurfaceForm() + " " + c.getLabel() + "\t\t\t" + c.getStartSpan());
        }
        SLModel model = new SLModel();
        model.lm = new Lexiconer();
        model.lm.setAllowNewFeatures(true);

        List<Pair<CorefMentionPair, CorefLabel>> instances = SLHelper.populateSLProblemForDocument(doc, model.lm,
                Parameters.COREF_MENTION_VIEW_GOLD, Parameters.COREF_VIEW_GOLD);

        for(Pair<CorefMentionPair, CorefLabel> instance : instances){
            System.out.println(instance.getFirst().getFirstConstituent().getSurfaceForm() + "\t" +
                               instance.getFirst().getSecondConstituent().getSurfaceForm() + "\t\t\t" +
                               instance.getSecond().getCorefLink());
        }
    }

    public static void testTesting(Corpus testCorpus){
        Document doc = testCorpus.getDoc(0);
        TextAnnotation ta = doc.getTA();
        CoreferenceView corefView = (CoreferenceView) ta.getView(Parameters.COREF_VIEW_PREDICTION);
        for(Constituent c : corefView.getConstituents()){
            System.out.println(c.getSurfaceForm() + "\t\t\t" + c.getLabel());
        }

    }
}