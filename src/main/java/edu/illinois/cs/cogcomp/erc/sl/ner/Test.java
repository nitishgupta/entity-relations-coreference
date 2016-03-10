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
}