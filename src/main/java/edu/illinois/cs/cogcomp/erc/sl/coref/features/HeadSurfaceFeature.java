package edu.illinois.cs.cogcomp.erc.sl.coref.features;

import edu.illinois.cs.cogcomp.erc.sl.coref.CorefLabel;
import edu.illinois.cs.cogcomp.erc.sl.coref.CorefMentionPair;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

/**
 * Created by nitishgupta on 5/10/16.
 */
public class HeadSurfaceFeature extends FeatureDefinitionBase {
    public HeadSurfaceFeature(Lexiconer lm) {
        super(lm);
    }

    // The structure will not be used : Solving a binary classification problem
    @Override
    public FeatureVectorBuffer getFeatureVector(CorefMentionPair instance, CorefLabel structure) {
        String head1 = instance.getFirstConstituent().getSurfaceForm().toLowerCase();
        String head2 = instance.getSecondConstituent().getSurfaceForm().toLowerCase();
        String label = structure.getCorefLink();

        String featurePrefix = this.featurePrefix;
        String feature1 = featurePrefix + "_" + head1 + "_" + head2 + "_" + label;
        String feature2 = featurePrefix + "_" + head2 + "_" + head1 + "_" + label;

        if (this.lexiconer.isAllowNewFeatures()) {
            // Add the NULL feature equivalent
            //this.lexiconer.addFeature(featurePrefix);
            this.lexiconer.addFeature(feature1);
            this.lexiconer.addFeature(feature2);
        }

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        if (this.lexiconer.containFeature(feature1)) {
            fvb.addFeature(this.lexiconer.getFeatureId(feature1), 1.0f);
        }
//        } else {
//            fvb.addFeature(this.lexiconer.getFeatureId(featurePrefix), 1.0f);
//        }

        if (this.lexiconer.containFeature(feature2)) {
            fvb.addFeature(this.lexiconer.getFeatureId(feature2), 1.0f);
        }
//        } else {
//            fvb.addFeature(this.lexiconer.getFeatureId(featurePrefix), 1.0f);
//        }

        return fvb;
    }
}
