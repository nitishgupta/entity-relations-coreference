package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.sl.ner.features.EmissionFeatures;
import edu.illinois.cs.cogcomp.erc.sl.ner.features.PriorFeatures;
import edu.illinois.cs.cogcomp.erc.sl.ner.features.TransitionFeatures;

import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
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

    /**
     * Returns the meta-feature generator based on the order of feature definitions.
     * @param lexiconer Lexiconer for generating features.
     * @return FeatureGenerator instance.
     */
    public static FeatureGenerator getCurrentFeatureGenerator(Lexiconer lexiconer) {
        // Modifying this will require re-training
        return new FeatureGenerator(Collections.unmodifiableList(Arrays.asList(
                new PriorFeatures(lexiconer),
                new EmissionFeatures(lexiconer),
                new TransitionFeatures(lexiconer)
        )));
    }

    public static void trainNER(Corpus trainData, String slConfigPath, String modelPath) throws Exception {
        SLModel model = new SLModel();
        model.lm = new Lexiconer();

        SLProblem slProblem = MainClass.readStructuredData(trainData, model.lm);

        // Disallow the creation of new features
        model.lm.setAllowNewFeatures(false);

        // Get our meta-feature generator with current set of features
        FeatureGenerator featureGenerator = getCurrentFeatureGenerator(model.lm);

        SLParameters parameters = new SLParameters();
        parameters.loadConfigFile(slConfigPath);
        parameters.TOTAL_NUMBER_FEATURE = featureGenerator.getFeatureVectorSize();

        // initialize the inference solver
        model.infSolver = new ViterbiInferenceSolver(model.lm, featureGenerator);

        Learner learner = LearnerFactory.getLearner(model.infSolver, featureGenerator, parameters);
        model.wv = learner.train(slProblem);
        WeightVector.printSparsity(model.wv);

        if(learner instanceof L2LossSSVMLearner)
            System.out.println("Primal objective:" +
                    ((L2LossSSVMLearner)learner).getPrimalObjective(slProblem, model.wv, model.infSolver, parameters.C_FOR_STRUCTURE));

        // save the model
        model.saveModel(modelPath);
    }
}
