package edu.illinois.cs.cogcomp.erc.sl.coref;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.sl.core.AbstractInferenceSolver;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Created by nitishgupta on 5/10/16.
 */
public class InferenceSolver extends AbstractInferenceSolver {
    private Lexiconer lm;
    private FeatureGenerator featureGenerator;

    public InferenceSolver(Lexiconer lm, FeatureGenerator fg) {
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

        CorefMentionPair ins = (CorefMentionPair) instance;
        CorefLabel gold = (CorefLabel) goldStructure;

        double linkScore, notLinkScore;

        // Solving binary classification. Label is not used for feature vector generation
        IFeatureVector fv_link = this.featureGenerator.getFeatureVector(instance, new CorefLabel("NULL"));
        linkScore = weightVector.dotProduct(fv_link);

        String bestStructure;
        if(linkScore > 5.0)
            bestStructure = CorefLabel.t;
        else
            bestStructure = CorefLabel.f;

        // Best loss structure : Invert if prediction is true
        if (gold != null && bestStructure.equals(gold.getCorefLink())) {
            if(bestStructure.equals(CorefLabel.t))
                bestStructure = CorefLabel.f;
            if(bestStructure.equals(CorefLabel.f))
                bestStructure = CorefLabel.t;
        }

        return new CorefLabel(bestStructure);
    }

    /* TODO : TRY MARGIN INFUSED LOSS */
    @Override
    public float getLoss(IInstance iInstance, IStructure goldStructure, IStructure predStructure) {
        CorefLabel gold = (CorefLabel) goldStructure;
        CorefLabel pred = (CorefLabel) predStructure;

        return Objects.equals(pred.getCorefLink(), gold.getCorefLink()) ? 0.0f : 1.0f;
    }

    @Override
    public Object clone() {
        return new InferenceSolver(lm, featureGenerator);
    }
}

