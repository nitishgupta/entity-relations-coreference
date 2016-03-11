package edu.illinois.cs.cogcomp.erc.sl.ner.features;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceLabel;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

/**
 * Created by Bhargav Mangipudi on 3/8/16.
 */
public class EmissionFeatures extends FeatureDefinitionBase {
    public EmissionFeatures(Lexiconer lm) {
        super(lm);
    }

    @Override
    public FeatureVectorBuffer getSparseFeature(SequenceInstance sequence, SequenceLabel label) {
        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        int idx = 0;
        for (Constituent c : sequence.getConstituents()) {
            int featureId;
            if (this.lexiconer.containFeature("w:"+ c.getSurfaceForm())) {
                featureId = this.lexiconer.getFeatureId("w:" + c.getSurfaceForm());
            } else {
                featureId = this.lexiconer.getFeatureId("w:unknownword");
            }

            fvb.addFeature(featureId + this.lexiconer.getNumOfFeature() * label.tagIds[idx++], 1.0f);
        }

        return fvb;
    }

    @Override
    public int getFeatureSize() {
        return this.lexiconer.getNumOfFeature() * this.lexiconer.getNumOfLabels();
    }
}
