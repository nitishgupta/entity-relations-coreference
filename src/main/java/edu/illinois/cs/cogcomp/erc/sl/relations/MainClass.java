package edu.illinois.cs.cogcomp.erc.sl.relations;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandDescription;
import edu.illinois.cs.cogcomp.core.utilities.commands.CommandIgnore;
import edu.illinois.cs.cogcomp.core.utilities.commands.InteractiveShell;
import edu.illinois.cs.cogcomp.edison.features.Feature;
import edu.illinois.cs.cogcomp.erc.config.ConfigSystem;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusType;
import edu.illinois.cs.cogcomp.erc.corpus.CorpusUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;

import edu.illinois.cs.cogcomp.erc.sl.ner.LexiconerConstants;
import edu.illinois.cs.cogcomp.erc.sl.relations.features.EntityTypeFeature;
import edu.illinois.cs.cogcomp.erc.sl.relations.features.FeatureDefinitionBase;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.core.*;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.learner.l2_loss_svm.L2LossSSVMLearner;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public class MainClass {

    public static Logger logger = LoggerFactory.getLogger(MainClass.class);

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
    public static void train(String corpusType) throws Exception { train(corpusType, "DefaultRelationModel"); }

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
            List<Pair<SLInstance, SLStructure>> instances = SLHelper.populateSLProblemForDocument(
                    doc,
                    model.lm,
                    ACEReader.ENTITYVIEW,
                    ACEReader.RELATIONVIEW);

            for (Pair<SLInstance, SLStructure> ins : instances) {
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
        model.infSolver = new ArgmaxInferenceSolver(model.lm, fg);
        SLParameters parameters = new SLParameters();

        parameters.loadConfigFile(Parameters.SL_PARAMETER_CONFIG_FILE);
        parameters.TOTAL_NUMBER_FEATURE = model.lm.getNumOfFeature();

        Learner learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, parameters);
        model.wv = learner.train(slProblem);

        WeightVector.printSparsity(model.wv);

        if(learner instanceof L2LossSSVMLearner)
            System.out.println("Primal objective:" +
                    ((L2LossSSVMLearner)learner).getPrimalObjective(slProblem, model.wv, model.infSolver, parameters.C_FOR_STRUCTURE));

        // save the model
        model.saveModel(modelFileName);
    }

    @CommandDescription(usage = "", description = "")
    public static void test(String corpusType, String modelFileName) throws Exception {
        SLModel slModel = SLModel.loadModel(modelFileName);
        testModel(corpusType, slModel);
    }

    @CommandDescription(usage = "", description = "")
    public static void trainAndTest(String corpusType) throws Exception { trainAndTest(corpusType, null); }

    @CommandDescription(usage = "", description = "")
    public static void trainAndTest(String corpusType, String modelFileName) throws Exception {
        train(corpusType);
        test(corpusType, modelFileName);
    }

    @CommandIgnore
    public static void testModel(String corpusType, SLModel modelInstance) throws Exception {
        Corpus aceCorpus = MainClass.readCorpus(corpusType);

        List<Corpus> allCorpora = CorpusUtils.getTrainDevTestCorpora(aceCorpus);
        Corpus trainCorpus = allCorpora.get(0);
        Corpus testCorpus = allCorpora.get(2);

        SLProblem slProblem = new SLProblem();
        for (Document doc : testCorpus.getDocs()) {
            List<Pair<SLInstance, SLStructure>> instances = SLHelper.populateSLProblemForDocument(
                    doc,
                    modelInstance.lm,
                    ACEReader.ENTITYVIEW,
                    ACEReader.RELATIONVIEW);

            for (Pair<SLInstance, SLStructure> ins : instances) {
                slProblem.addExample(ins.getFirst(), ins.getSecond());
            }
        }

        TestDiscrete test = new TestDiscrete();
        test.addNull(SLHelper.NO_RELATION_LABEL);

        for (Pair<IInstance, IStructure> ins : slProblem) {
            SLStructure gold = (SLStructure) ins.getSecond();
            SLStructure pred = (SLStructure) modelInstance.infSolver.getBestStructure(modelInstance.wv, ins.getFirst());

            test.reportPrediction(pred.getRelationLabel(), gold.getRelationLabel());
        }

        test.printPerformance(System.out);
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
    }
}
