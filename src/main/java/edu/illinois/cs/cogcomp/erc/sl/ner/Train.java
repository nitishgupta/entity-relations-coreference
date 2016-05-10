package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
//import edu.illinois.cs.cogcomp.erc.sl.ner.features.*;

import edu.illinois.cs.cogcomp.sl.core.*;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.learner.l2_loss_svm.L2LossSSVMLearner;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by nitishgupta on 3/9/16.
 */
public class Train {

//    /**
//     * Returns the meta-feature generator based on the order of feature definitions.
//     * @param lexiconer Lexiconer for generating features.
//     * @return FeatureGenerator instance.
//     */
//    public static FeatureGenerator getCurrentFeatureGenerator(Lexiconer lexiconer) {
//        // Modifying this will require re-training
//        return new FeatureGenerator(Collections.unmodifiableList(Arrays.asList(
//                new PriorFeatures(lexiconer),
//                new EmissionFeatures(lexiconer),
//                new TransitionFeatures(lexiconer),
//                new CurrentWordPOSFeature(lexiconer),
//                new CurrentWordCapitalizationFeature(lexiconer)
//        )));
//    }

    public static void trainNER(Corpus trainData, String slConfigPath, String modelPath, String viewName) throws Exception {
        SLModel model = new SLModel();
        model.lm = new Lexiconer();
        model.lm.setAllowNewFeatures(true);

        if (model.lm.isAllowNewFeatures()) {
            // To act as Unknown feature
            model.lm.addFeature(LexiconerConstants.WORD_UNKNOWN);
        }
        model.featureGenerator = new FeatureGenerator(model.lm);

        // Read the training data into IInstance and IStructure || IStructure=string[] "label" || lm.label=PREFIX+"label"
        SLProblem slProblem = MainClass.readStructuredData(trainData, model.lm, viewName);

        // Get our meta-feature generator with current set of features
        //FeatureGenerator featureGenerator = getCurrentFeatureGenerator(model.lm);
        pre_extract(model, slProblem);

        //Extraction Done
        System.out.println("Number of Features : " + model.lm.getNumOfFeature());
        System.out.println("Number of Labels : " + model.lm.getNumOfLabels());

        // Disallow the creation of new features
        model.lm.setAllowNewFeatures(false);

        // initialize the inference solver
        model.infSolver = new ViterbiInferenceSolver(model.lm, model.featureGenerator);
        SLParameters parameters = new SLParameters();
        parameters.loadConfigFile(slConfigPath);
        parameters.TOTAL_NUMBER_FEATURE = model.lm.getNumOfFeature();

        Learner learner = LearnerFactory.getLearner(model.infSolver, model.featureGenerator, parameters);
        model.wv = learner.train(slProblem);
        WeightVector.printSparsity(model.wv);

        if(learner instanceof L2LossSSVMLearner)
            System.out.println("Primal objective:" +
                    ((L2LossSSVMLearner)learner).getPrimalObjective(slProblem, model.wv, model.infSolver, parameters.C_FOR_STRUCTURE));

        // save the model
        model.saveModel(modelPath);
    }

    private static void pre_extract(SLModel model, SLProblem problem) {
        for (Pair<IInstance, IStructure> p : problem) {
            model.featureGenerator
                    .getFeatureVector(p.getFirst(), p.getSecond());
        }
    }
}
