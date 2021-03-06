package edu.illinois.cs.cogcomp.erc.sl.ner.features;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.edison.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory;
import edu.illinois.cs.cogcomp.erc.sl.ner.LexiconerConstants;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.List;

/**
 * Created by Bhargav Mangipudi on 3/8/16.
 */
public class EmissionFeatures extends FeatureDefinitionBase {
    public EmissionFeatures(Lexiconer lm) {
        super(lm);
    }

    @Override
    public void addLocalFeature(
            List<Pair<String, String>> featureMap,
            SequenceInstance sentence,
            String currentLabel,
            String prevLabel,
            int position) {
        featureMap.add(new Pair<>(
                LexiconerConstants.WORD_PREFIX + sentence.getTokenAtPosition(position).toLowerCase().trim(),
                currentLabel));
    }
}
