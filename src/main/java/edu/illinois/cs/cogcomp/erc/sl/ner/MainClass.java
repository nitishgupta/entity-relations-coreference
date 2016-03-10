package edu.illinois.cs.cogcomp.erc.sl.ner;

import com.google.common.collect.ImmutableList;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.sl.ner.features.EmissionFeatures;
import edu.illinois.cs.cogcomp.erc.sl.ner.features.PriorFeatures;
import edu.illinois.cs.cogcomp.erc.sl.ner.features.TransitionFeatures;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLParameters;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.learner.Learner;
import edu.illinois.cs.cogcomp.sl.learner.LearnerFactory;
import edu.illinois.cs.cogcomp.sl.learner.l2_loss_svm.L2LossSSVMLearner;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;

/**
 * Created by Bhargav Mangipudi on 3/9/16.
 */
public class MainClass {
    /**
     * Returns the meta-feature generator based on the order of feature definitions.
     * @param lexiconer Lexiconer for generating features.
     * @return FeatureGenerator instance.
     */
    public static FeatureGenerator getCurrentFeatureGenerator(Lexiconer lexiconer) {
        // Modifying this will require re-training

        return new FeatureGenerator(ImmutableList.of(
                new PriorFeatures(lexiconer),
                new EmissionFeatures(lexiconer),
                new TransitionFeatures(lexiconer)));
    }

    public static void trainNER(Corpus trainData, String slConfigPath, String modelPath) throws Exception {
        SLModel model = new SLModel();
        model.lm = new Lexiconer();

        SLProblem slProblem = null; // Nitish's method plug

        // Disallow the creation of new features
        model.lm.setAllowNewFeatures(false);

        // initialize the inference solver
//        model.infSolver = new ViterbiInferenceSolver(model.lm);

        // Get our meta-feature generator with current set of features
        FeatureGenerator featureGenerator = getCurrentFeatureGenerator(model.lm);

        SLParameters parameters = new SLParameters();
        parameters.loadConfigFile(slConfigPath);
        parameters.TOTAL_NUMBER_FEATURE = featureGenerator.getFeatureVectorSize();

        Learner learner = LearnerFactory.getLearner(model.infSolver, featureGenerator, parameters);
        model.wv = learner.train(slProblem);
        WeightVector.printSparsity(model.wv);

        if(learner instanceof L2LossSSVMLearner)
            System.out.println("Primal objective:" +
                    ((L2LossSSVMLearner)learner).getPrimalObjective(slProblem, model.wv, model.infSolver, parameters.C_FOR_STRUCTURE));

        // save the model
        model.saveModel(modelPath);
    }

    public static void testNER(Corpus testData, String modelPath) throws Exception {
        SLModel model = SLModel.loadModel(modelPath);
        SLProblem slProblem = null; // Nitish's method plug

        TestDiscrete testDiscreteRaw = new TestDiscrete();
        TestDiscrete testDiscreteFormatted = new TestDiscrete();

        for (int i = 0; i < slProblem.instanceList.size(); i++) {

            SequenceLabel gold = (SequenceLabel) slProblem.goldStructureList.get(i);
            SequenceLabel prediction = (SequenceLabel) model.infSolver.getBestStructure(
                    model.wv,
                    slProblem.instanceList.get(i));

            for (int j = 0; j < prediction.tagIds.length; j++) {
                testDiscreteRaw.reportPrediction(prediction.tagIds[j] + "", gold.tagIds[j] + "");

                testDiscreteFormatted.reportPrediction(
                        model.lm.getLabelString(prediction.tagIds[j]),
                        model.lm.getLabelString(gold.tagIds[j]));
            }
        }

        System.out.print("Raw Performance Metrics");
        testDiscreteRaw.printPerformance(System.out);

        System.out.print("Formatted Performance Metrics");
        testDiscreteFormatted.printPerformance(System.out);
    }

    public static void Main(String[] args) {

    }
}
