package edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.features;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Queries;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationLabel;
import edu.illinois.cs.cogcomp.erc.sl.relations.pairwise.RelationMentionPair;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public class MiscLexicalFeatures extends FeatureDefinitionBase {
    public MiscLexicalFeatures(Lexiconer lm) {
        super(lm);
    }

    @Override
    public FeatureVectorBuffer getFeatureVector(RelationMentionPair instance, RelationLabel structure) {
        String nullFeature = this.featurePrefix + "_" + structure.getRelationLabel();
        List<String> wordFeatures = new ArrayList<>(5);

        Constituent firstMention = instance.getFirstMention();
        TextAnnotation ta = firstMention.getTextAnnotation();

        int fmStart = firstMention.getStartSpan();

        if (fmStart > 0) {
            wordFeatures.add("BM1F:" + ta.getToken(fmStart - 1).toLowerCase().trim());
        }

        if (fmStart > 1) {
            wordFeatures.add("BM1L:" + ta.getToken(fmStart - 2).toLowerCase().trim());
        }

        Constituent secondMention = instance.getSecondMention();

        int smEnd = secondMention.getEndSpan();

        if (smEnd < ta.getTokens().length - 1) {
            wordFeatures.add("AM2F:" + ta.getToken(smEnd).toLowerCase().trim());
        }

        if (smEnd < ta.getTokens().length - 2) {
            wordFeatures.add("AM2L:" + ta.getToken(smEnd + 1).toLowerCase().trim());
        }

        if (firstMention.getEndSpan() == secondMention.getStartSpan()) {
            wordFeatures.add("WBNULL");
        }

        if (this.lexiconer.isAllowNewFeatures()) {
            this.lexiconer.addFeature(nullFeature);
            for (String feat : wordFeatures) {
                this.lexiconer.addFeature(nullFeature + "_" + feat);
            }
        }

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        for (String feat : wordFeatures) {

            if (this.lexiconer.containFeature(nullFeature + "_" + feat)) {
                fvb.addFeature(this.lexiconer.getFeatureId(nullFeature + "_" + feat), 1.0f);
            } else {
                fvb.addFeature(this.lexiconer.getFeatureId(nullFeature), 1.0f);
            }
        }

        return fvb;
    }
}
