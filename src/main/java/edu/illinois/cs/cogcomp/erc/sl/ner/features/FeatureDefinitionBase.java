package edu.illinois.cs.cogcomp.erc.sl.ner.features;

import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceLabel;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.io.Serializable;

/**
 * Created by Bhargav Mangipudi on 3/8/16.
 */
public abstract class FeatureDefinitionBase implements Serializable {
    protected Lexiconer lexiconer;

    public FeatureDefinitionBase(Lexiconer lm) {
        assert !lm.isAllowNewFeatures() : "Lexiconer should not be modifiable while defining features";
        this.lexiconer = lm;
    }

    public abstract FeatureVectorBuffer getSparseFeature(SequenceInstance sequence, SequenceLabel label);

    // Size or number of features
    // Value depends on the size of lexiconer and is to be used only after adding all features
    public abstract int getFeatureSize();
}
