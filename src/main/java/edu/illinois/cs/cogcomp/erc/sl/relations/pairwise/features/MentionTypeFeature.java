package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.features;

import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationMentionPair;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationLabel;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

/**
 * @author Bhargav Mangipudi
 */
public class MentionTypeFeature extends FeatureDefinitionBase {
    public MentionTypeFeature(Lexiconer lm) {
        super(lm);
    }

    @Override
    public FeatureVectorBuffer getFeatureVector(RelationMentionPair instance, RelationLabel structure) {
        String nullFeature = this.featurePrefix + "_" + structure.getRelationLabel();
        String feature = nullFeature + "_" +
                instance.getFirstMention().getLabel() + "_" +
                instance.getSecondMention().getLabel();

        if (this.lexiconer.isAllowNewFeatures()) {
            // Add the NULL feature equivalent
            this.lexiconer.addFeature(nullFeature);
            this.lexiconer.addFeature(feature);
        }

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        if (this.lexiconer.containFeature(feature)) {
            fvb.addFeature(this.lexiconer.getFeatureId(feature), 1.0f);
        } else {
            fvb.addFeature(this.lexiconer.getFeatureId(nullFeature), 1.0f);
        }

        return fvb;
    }
}
