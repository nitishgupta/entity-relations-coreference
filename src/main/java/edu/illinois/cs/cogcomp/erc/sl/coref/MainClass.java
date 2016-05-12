package edu.illinois.cs.cogcomp.erc.sl.coref;


import edu.illinois.cs.cogcomp.annotation.Annotator;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.CoreferenceView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.experiments.ClassificationTester;
import edu.illinois.cs.cogcomp.core.experiments.evaluators.CorefBCubedEvaluator;
import edu.illinois.cs.cogcomp.core.utilities.Table;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandIgnore;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusType;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusUtils;
import edu.illinois.cs.cogcomp.erc.sl.ner.annotators.NERAnnotator;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.sl.coref.annotators.GoldMentionAnnotator;
import edu.illinois.cs.cogcomp.erc.util.ChainedAnnotator;
import edu.illinois.cs.cogcomp.openeval.learner.Server;
import edu.illinois.cs.cogcomp.openeval.learner.ServerPreferences;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.learner.l2_loss_svm.L2LossSSVMLearner;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;
import fi.iki.elonen.util.ServerRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

import static edu.illinois.cs.cogcomp.erc.sl.ner.MainClass.DefaultNERModel;

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

    @CommandDescription(usage = "testP", description = "")
    public static void testPredicted(String corpusType) throws Exception {
        SLModel corefModel = SLModel.loadModel(DefaultCorefModel);
        SLModel nerModel = SLModel.loadModel(DefaultNERModel);
        testModelPredicted(corpusType, nerModel, corefModel);
    }

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


        ClassificationTester clfTester = new ClassificationTester();
        CorefBCubedEvaluator bcubed = new CorefBCubedEvaluator();

        String corefGoldView = Parameters.COREF_VIEW_GOLD;
        String corefPredictedView = Parameters.COREF_VIEW_PREDICTION;
        String mentionView = Parameters.COREF_MENTION_VIEW_GOLD;

        // Annotator instance is used to create the predicted view in the textAnnotation.
        Annotator annotator = new GoldMentionAnnotator(
                corefPredictedView,                                                // Populated Final View
                new String[] { ViewNames.POS, ViewNames.TOKENS, mentionView },
                modelInstance,
                mentionView,                                                     // Mention View Required
                aceCorpus.checkisACE2004());

        System.out.println("ANNOTATOR IS MADE");

        // Annotate a TA and evaluate its performance.
        for (Document doc : testCorpus.getDocs()) {
            TextAnnotation ta = doc.getTA();
            annotator.addView(ta);

            bcubed.evaluate(clfTester, ta.getView(corefGoldView), ta.getView(corefPredictedView));
        }

        //CorefEvaluator.writeChainSolutionToFile(testCorpus, corefGoldView, "gold_coref.txt");
        CorefEvaluator.writeChainSolutionToFile(testCorpus, corefPredictedView, "pred_coref_goldm.txt");

        // Print the performance table.
        Table performanceTable = clfTester.getPerformanceTable(true);
        System.out.println(performanceTable.toOrgTable());

        //testTesting(testCorpus);
    }
    @CommandIgnore
    public static void testModelPredicted(String corpusType, SLModel nerModel, SLModel modelInstance) throws Exception {
        Corpus aceCorpus = MainClass.readCorpus(corpusType);

        List<Corpus> allCorpora = CorpusUtils.getTrainDevTestCorpora(aceCorpus);
        Corpus testCorpus = allCorpora.get(2);
        String corefGoldView = Parameters.COREF_VIEW_GOLD;
        String corefPredictedView = Parameters.COREF_VIEW_PREDICTION;
        String predMentionView = "ENTITYVIEW";

        Annotator NERAnnotator = new NERAnnotator(
                nerModel,
                testCorpus.checkisACE2004(),
                "NER_BIO",
                predMentionView);          // POPULATED FINAL VIEW


        ClassificationTester clfTester = new ClassificationTester();
        CorefBCubedEvaluator bcubed = new CorefBCubedEvaluator();

        // Annotator instance is used to create the predicted view in the textAnnotation.
        Annotator corefannotator = new GoldMentionAnnotator(
                corefPredictedView,                                                // Populated Final View
                new String[] { ViewNames.POS, ViewNames.TOKENS, predMentionView },
                modelInstance,
                predMentionView,                                                     // Mention View Required
                aceCorpus.checkisACE2004());

        ChainedAnnotator annotator = new ChainedAnnotator(NERAnnotator, corefannotator);

        System.out.println("CHAINED ANNOTATOR IS MADE");

        // Annotate a TA and evaluate its performance.
        for (Document doc : testCorpus.getDocs()) {
            TextAnnotation ta = doc.getTA();
            annotator.addView(ta);

            bcubed.evaluate(clfTester, ta.getView(corefGoldView), ta.getView(corefPredictedView));
        }

        //CorefEvaluator.writeChainSolutionToFile(testCorpus, corefGoldView, "gold_coref.txt");
        CorefEvaluator.writeChainSolutionToFile(testCorpus, corefPredictedView, "pred_coref_predm.txt");

        // Print the performance table.
        Table performanceTable = clfTester.getPerformanceTable(true);
        System.out.println(performanceTable.toOrgTable());

        //testTesting(testCorpus);
    }

    @CommandDescription(usage = "", description = "")
    public static void annotatePipeline(String corpusType, String entityModelFile,
                                        String corefModelFile) throws Exception {
        SLModel entityModel = SLModel.loadModel(entityModelFile);
        SLModel corefModel = SLModel.loadModel(corefModelFile);

        Annotator NERAnnotator = new NERAnnotator(
                entityModel,
                false,                  // Check for ACE04
                "NER_BIO",
                "ENTITYVIEW");

        String corefPredictedView = Parameters.COREF_VIEW_GOLD;

        // Annotator instance is used to create the predicted view in the textAnnotation.
        Annotator GoldMentionAnnotator = new GoldMentionAnnotator(
                corefPredictedView,                                                // Predicted Final View
                new String[] { ViewNames.POS, ViewNames.TOKENS, "ENTITYVIEW" },
                corefModel,
                "ENTITYVIEW",
                false);                                                             // ACE04 Check

        ChainedAnnotator annotator = new ChainedAnnotator(NERAnnotator, GoldMentionAnnotator);

        // Annotate a TA and evaluate its performance.
//        for (Document doc : testCorpus.getDocs()) {
//            TextAnnotation ta = doc.getTA();
//            annotator.addView(ta);
//        }

        //Server client = new Server(5757, new ServerPreferences(10000, 1), GoldMentionAnnotator);
        Server client = new Server(5757, new ServerPreferences(10000, 1), annotator);

        ServerRunner.executeInstance(client);
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

        //test("ACE05", DefaultCorefModel);



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
        Document doc = testCorpus.getDoc(10);
        TextAnnotation ta = doc.getTA();
        CoreferenceView corefView = (CoreferenceView) ta.getView(Parameters.COREF_VIEW_PREDICTION);
        for(Constituent c : corefView.getConstituents()){
            System.out.println(c.getSurfaceForm() + "\t\t\t" + c.getLabel());
        }
    }
}