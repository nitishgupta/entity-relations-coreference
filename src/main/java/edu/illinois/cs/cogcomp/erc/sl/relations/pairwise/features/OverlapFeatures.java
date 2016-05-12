package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.features;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Queries;
import edu.illinois.cs.cogcomp.core.transformers.Predicate;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationLabel;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationMentionPair;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public class OverlapFeatures extends FeatureDefinitionBase {
    public OverlapFeatures(Lexiconer lm) {
        super(lm);
    }

    @Override
    public FeatureVectorBuffer getFeatureVector(RelationMentionPair instance, RelationLabel structure) {
        String nullFeature = this.featurePrefix + "_" + structure.getRelationLabel();
        List<String> overlapFeatures = new ArrayList<>(4);

        Constituent firstMention = instance.getFirstMention();
        Constituent secondMention = instance.getSecondMention();

        Predicate<Constituent> hasOverlap = Queries.hasOverlap(firstMention);

        Boolean overlap = hasOverlap.transform(secondMention);
        String et12 = firstMention.getClass() + "_" + secondMention.getClass();

        overlapFeatures.add(overlap.toString());
        overlapFeatures.add(overlap + "_" + et12);

        if (!overlap) {
            if (firstMention.getEndSpan() < secondMention.getStartSpan()) {
                overlapFeatures.add("M1<M2");
                overlapFeatures.add("M1<M2:" + et12);
            } else if (secondMention.getEndSpan() < firstMention.getStartSpan()) {
                overlapFeatures.add("M1>M2");
                overlapFeatures.add("M1>M2:" + et12);
            }
        }

        if (this.lexiconer.isAllowNewFeatures()) {
            this.lexiconer.addFeature(nullFeature);
            for (String feat : overlapFeatures) {
                this.lexiconer.addFeature(nullFeature + "_" + feat);
            }
        }

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        for (String feat : overlapFeatures) {

            if (this.lexiconer.containFeature(nullFeature + "_" + feat)) {
                fvb.addFeature(this.lexiconer.getFeatureId(nullFeature + "_" + feat), 1.0f);
            } else {
                fvb.addFeature(this.lexiconer.getFeatureId(nullFeature), 1.0f);
            }
        }

        return fvb;
    }
}
