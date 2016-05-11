package edu.illinois.cs.cogcomp.erc.sl.coref.features;

import edu.illinois.cs.cogcomp.erc.sl.coref.CorefLabel;
import edu.illinois.cs.cogcomp.erc.sl.coref.CorefMentionPair;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

/**
 * Created by nitishgupta on 5/10/16.
 */
public class MentionTypeFeature extends FeatureDefinitionBase {
    public MentionTypeFeature(Lexiconer lm) {
        super(lm);
    }

    // The structure will not be used : Solving a binary classification problem
    @Override
    public FeatureVectorBuffer getFeatureVector(CorefMentionPair instance, CorefLabel structure) {
        String featurePrefix = this.featurePrefix;
        String feature = featurePrefix + "_" +
                instance.getFirstConstituent().getAttribute(ACEReader.EntityMentionTypeAttribute) + "_" +
                instance.getSecondConstituent().getAttribute(ACEReader.EntityMentionTypeAttribute);

        if (this.lexiconer.isAllowNewFeatures()) {
            // Add the NULL feature equivalent
            this.lexiconer.addFeature(featurePrefix);
            this.lexiconer.addFeature(feature);
        }

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        if (this.lexiconer.containFeature(feature)) {
            fvb.addFeature(this.lexiconer.getFeatureId(feature), 1.0f);
        } else {
            fvb.addFeature(this.lexiconer.getFeatureId(featurePrefix), 1.0f);
        }

        return fvb;
    }
}

