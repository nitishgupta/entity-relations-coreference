package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.features.ContextFeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.Feature;
import edu.illinois.cs.cogcomp.edison.features.FeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.NgramFeatureExtractor;
import edu.illinois.cs.cogcomp.edison.features.factory.WordFeatureExtractorFactory;
import edu.illinois.cs.cogcomp.edison.features.helpers.WordHelpers;
import edu.illinois.cs.cogcomp.edison.utilities.EdisonException;
import edu.illinois.cs.cogcomp.erc.sl.ner.features.FeatureDefinitionBase;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Bhargav Mangipudi on 3/8/16.
 */
public class FeatureGenerator extends AbstractFeatureGenerator implements Serializable {
    private static final long serialVersionUID = 8246812640996043593L;
    private Lexiconer lexiconer;
    private List<FeatureDefinitionBase> activeFeatures;

    /**
     * @param lm : Lexiconer to add features into
     */
    public FeatureGenerator(List<FeatureDefinitionBase> activeFeatures, Lexiconer lm) {
        this.activeFeatures = Collections.unmodifiableList(activeFeatures);
        this.lexiconer = lm;
    }

    /**
     * Feature generating function
     *
     * @param x
     * @param y
     * @return a feature vector based on an instance-structure pair (x,y).
     */
    @Override
    public IFeatureVector getFeatureVector(IInstance x, IStructure y) {
        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        for (FeatureDefinitionBase fb : this.activeFeatures) {
            SequenceInstance ins = (SequenceInstance) x;
            SequenceLabel sl = (SequenceLabel) y;

            fvb.addFeature(fb.getFeatureVector(ins, sl));
        }

        return fvb.toFeatureVector();
    }

    public FeatureVectorBuffer getLocalFeatureVector(
            SequenceInstance sentence,
            String currentLabel,
            String previousLabel,
            int pos) {

        FeatureVectorBuffer fvb = new FeatureVectorBuffer();

        // Workaround to get this working presently.
        for (FeatureDefinitionBase fb : this.activeFeatures) {
            fvb.addFeature(fb.getLocalFeatureVector(sentence, currentLabel, previousLabel, pos));
        }

        return fvb;
    }
}
