package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.erc.sl.ner.features.FeatureDefinitionBase;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;

import java.util.Collections;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 3/8/16.
 */
public class FeatureGenerator extends AbstractFeatureGenerator {
    List<FeatureDefinitionBase> activeFeatures;

    /**
     * @param listOfFeatures List of Feature Definitions to be used
     */
    public FeatureGenerator(List<FeatureDefinitionBase> listOfFeatures) {
        assert !listOfFeatures.isEmpty() : "Must define at least one feature";
        this.activeFeatures = Collections.unmodifiableList(listOfFeatures);
    }

    @Override
    public IFeatureVector getFeatureVector(IInstance iInstance, IStructure iStructure) {
        SequenceInstance sequence = (SequenceInstance)iInstance;
        SequenceLabel label = (SequenceLabel)iStructure;

        int shift = 0;
        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        for (FeatureDefinitionBase feature : this.activeFeatures) {
            fvb.addFeature(feature.getSparseFeature(sequence, label), shift);
            shift += feature.getFeatureSize();
        }

        return fvb.toFeatureVector();
    }

    /**
     * Returns the size of the final feature vector.
     * @return Size of the final FeatureVector
     */
    public int getFeatureVectorSize() {
        int size = 0;

        for (FeatureDefinitionBase feature : this.activeFeatures) {
            size += feature.getFeatureSize();
        }

        return size;
    }
}