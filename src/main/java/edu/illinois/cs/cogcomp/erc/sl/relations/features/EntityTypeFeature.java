package edu.illinois.cs.cogcomp.erc.sl.relations.features;

import edu.illinois.cs.cogcomp.erc.sl.relations.SLInstance;
import edu.illinois.cs.cogcomp.erc.sl.relations.SLStructure;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

/**
 * @author Bhargav Mangipudi
 */
public class EntityTypeFeature extends FeatureDefinitionBase {
    private String featurePrefix;

    public EntityTypeFeature(Lexiconer lm) {
        super(lm);
        this.featurePrefix = this.getClass().getName();
    }

    @Override
    public FeatureVectorBuffer getFeatureVector(SLInstance instance, SLStructure structure) {
        String nullFeature = this.featurePrefix + "_" + structure.getRelationLabel();
        String feature = nullFeature + "_" +
                instance.getFirstMention().getAttribute(ACEReader.EntityMentionTypeAttribute) + "_" +
                instance.getSecondMention().getAttribute(ACEReader.EntityMentionTypeAttribute);

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
