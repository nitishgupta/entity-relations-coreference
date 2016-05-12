package edu.illinois.cs.cogcomp.erc.sl.ner.features;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.edison.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.factory.ChunkEmbedding;
import edu.illinois.cs.cogcomp.erc.sl.ner.SequenceInstance;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public class ChunkEmbeddingFeature extends FeatureDefinitionBase {
    private static FeatureExtractor fex = ChunkEmbedding.SHALLOW_PARSE;

    public ChunkEmbeddingFeature(Lexiconer lm) {
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
