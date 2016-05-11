package edu.illinois.cs.cogcomp.erc.sl.ner.features;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.edison.features.ContextFeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.Feature;
import edu.illinois.cs.cogcomp.edison.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory;
import edu.illinois.cs.cogcomp.edison.utilities.EdisonException;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.List;
import java.util.Set;

/**
 * @author Bhargav Mangipudi
 */
public class ContextWindowTokenFeature extends FeatureDefinitionBase {
    private static FeatureExtractor contextTokenFex = new ContextFeatureExtractor(2, false, false, WordFeatureExtractorFactory.word);

    public ContextWindowTokenFeature(Lexiconer lm) {
        super(lm);
    }

    @Override
    public void addLocalFeature(
            List<Pair<String, String>> featureMap,
            SequenceInstance sentence,
            String currentLabel,
            String prevLabel,
            int position) {
        Constituent c = sentence.getConstituents().get(position);
        FeatureDefinitionBase.updateFeatureMapFromFex(featureMap, contextTokenFex, c, currentLabel);
    }
}
