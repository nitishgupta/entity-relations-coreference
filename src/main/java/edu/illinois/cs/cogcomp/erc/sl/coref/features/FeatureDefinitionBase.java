package edu.illinois.cs.cogcomp.erc.sl.coref.features;

import edu.illinois.cs.cogcomp.erc.sl.coref.CorefLabel;
import edu.illinois.cs.cogcomp.erc.sl.coref.CorefMentionPair;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.io.Serializable;

/**
 * Created by nitishgupta on 5/10/16.
 */

public abstract class FeatureDefinitionBase extends AbstractFeatureGenerator implements Serializable {
    protected final Lexiconer lexiconer;
    protected final String featurePrefix;

    public FeatureDefinitionBase(Lexiconer lm) {
        this.lexiconer = lm;
        this.featurePrefix = this.getClass().getName();
    }

    // Assumes lexicon is populated already.
    public void preExtractFeatures(CorefMentionPair instance) {
        // Do not need a label : Solving a binary classification problem
        this.getFeatureVector(instance, new CorefLabel(CorefLabel.t));
        this.getFeatureVector(instance, new CorefLabel(CorefLabel.f));
    }

    public IFeatureVector getFeatureVector(IInstance x, IStructure y) {
        CorefMentionPair ins = (CorefMentionPair) x;
        CorefLabel str = (CorefLabel) y;

        return getFeatureVector(ins, str).toFeatureVector();
    }

    public abstract FeatureVectorBuffer getFeatureVector(CorefMentionPair instance, CorefLabel structure);

}

