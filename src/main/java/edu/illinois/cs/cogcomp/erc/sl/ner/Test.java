package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.erc.corpus.Corpus;

import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;

import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;

/**
 * Created by nitishgupta on 3/9/16.
 */
public class Test {

    public static void testNER(Corpus testData, String modelPath) throws Exception {
        SLModel model = SLModel.loadModel(modelPath);

        // Disable modification of lexicon while testing.
        model.lm.setAllowNewFeatures(false);

        SLProblem slProblem = MainClass.readStructuredData(testData, model.lm, Corpus.NER_GOLD_HEAD_BIO_VIEW);

        TestDiscrete testDiscreteFormatted = new TestDiscrete();
        TestDiscrete testFiltered = new TestDiscrete();
        //testFiltered.addNull(LexiconerConstants.LABEL_PREFIX + "O");
        testFiltered.addNull("O");

        for (int i = 0; i < slProblem.instanceList.size(); i++) {

            SequenceLabel gold = (SequenceLabel) slProblem.goldStructureList.get(i);
            SequenceLabel prediction = (SequenceLabel) model.infSolver.getBestStructure(
                    model.wv,
                    slProblem.instanceList.get(i));

            for (int j = 0; j < prediction.labels.length; j++) {
                testDiscreteFormatted.reportPrediction(
                        prediction.getLabelAtPosition(j),
                        gold.getLabelAtPosition(j));

                testFiltered.reportPrediction(
                        prediction.getLabelAtPosition(j),
                        gold.getLabelAtPosition(j));
            }
        }

        System.out.println("Formatted Performance Metrics");
        testDiscreteFormatted.printPerformance(System.out);

        System.out.println("Filtered Performance Metrics");
        testFiltered.printPerformance(System.out);

        System.out.print(model.featureGenerator);
    }
}
