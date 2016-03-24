package edu.illinois.cs.cogcomp.erc.sl.ner.features;

import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceLabel;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;

import java.io.Serializable;

/**
 * Created by Bhargav Mangipudi on 3/12/16.
 */
public interface IFeatureDefinition extends Serializable {
    FeatureVectorBuffer getSparseFeature(SequenceInstance sequence, SequenceLabel label);

    // Size or number of features
    // Value depends on the size of lexiconer and is to be used only after adding all features
    int getFeatureSize();

    // Get the value of local discriminant function score for a given feature
    // Note: Currently only supports first-order label dependencies.
    FeatureVectorBuffer getLocalScore(SequenceInstance sequence,
                                      int currentWordPosition,
                                      int prevLabelId,
                                      int currentLabelId);
}
