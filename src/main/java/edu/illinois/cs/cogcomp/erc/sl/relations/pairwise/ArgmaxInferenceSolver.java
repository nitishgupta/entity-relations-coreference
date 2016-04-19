package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.sl.core.AbstractInferenceSolver;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;

import java.util.*;

/**
 * @author Bhargav Mangipudi
 */
public class ArgmaxInferenceSolver extends AbstractInferenceSolver {
    private Lexiconer lm;
    private FeatureGenerator featureGenerator;

    public ArgmaxInferenceSolver(Lexiconer lm, FeatureGenerator fg) {
        this.lm = lm;
        this.featureGenerator = fg;
    }

    @Override
    public IStructure getBestStructure(WeightVector weightVector, IInstance iInstance) throws Exception {
        return getLossAugmentedBestStructure(weightVector, iInstance, null);
    }

    @Override
    public IStructure getLossAugmentedBestStructure(
            WeightVector weightVector,
            IInstance instance,
            IStructure goldStructure) throws Exception {
        assert !this.lm.isAllowNewFeatures();

        RelationMentionPair ins = (RelationMentionPair) instance;
        RelationLabel gold = (RelationLabel) goldStructure;

        List<Pair<String, Float>> scores = new ArrayList<>(this.lm.getNumOfLabels());

        int numLabels = this.lm.getNumOfLabels();
        for (int i = 0; i < numLabels; i++) {
            String currentLabel = this.lm.getLabelString(i);

            IFeatureVector fv = this.featureGenerator.getFeatureVector(instance, new RelationLabel(currentLabel));
            scores.add(new Pair<>(currentLabel, weightVector.dotProduct(fv)));
        }

        scores.sort(new Comparator<Pair<String, Float>>() {

            // Sort max first.
            @Override
            public int compare(Pair<String, Float> o1, Pair<String, Float> o2) {
                return -1 * o1.getSecond().compareTo(o2.getSecond());
            }
        });

        Pair<String, Float> bestStructure = scores.get(0);

        // Skip the best structure
        if (gold != null && bestStructure.getFirst().equals(gold.getRelationLabel())) {
            bestStructure = scores.get(1);
        }

        return new RelationLabel(bestStructure.getFirst());
    }

    @Override
    public float getLoss(IInstance iInstance, IStructure goldStructure, IStructure predStructure) {
        RelationLabel gold = (RelationLabel) goldStructure;
        RelationLabel pred = (RelationLabel) predStructure;

        return Objects.equals(pred.getRelationLabel(), gold.getRelationLabel()) ? 0.0f : 1.0f;
    }

    @Override
    public Object clone() {
        return new ArgmaxInferenceSolver(lm, featureGenerator);
    }
}
