package edu.illinois.cs.cogcomp.erc.sl.ner.features;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.edison.features.FeatureCollection;
import edu.illinois.cs.cogcomp.edison.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory;
import edu.illinois.cs.cogcomp.erc.sl.ner.LexiconerConstants;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceLabel;
import edu.illinois.cs.cogcomp.erc.util.Utils;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.List;

/**
 * Created by Bhargav Mangipudi on 3/27/16.
 */
public class CurrentWordPOSFeature extends FeatureDefinitionBase {
    private static FeatureExtractor fex = new FeatureCollection("CurrentWordPOSFeature", WordFeatureExtractorFactory.pos,
            WordFeatureExtractorFactory.conflatedPOS);

    public CurrentWordPOSFeature(Lexiconer lm) {
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
        FeatureDefinitionBase.updateFeatureMapFromFex(featureMap, fex, c, currentLabel);
    }
}
