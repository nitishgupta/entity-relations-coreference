package edu.illinois.cs.cogcomp.erc.sl.relations.features;

import edu.illinois.cs.cogcomp.erc.sl.relations.SLInstance;
import edu.illinois.cs.cogcomp.erc.sl.relations.SLStructure;
import edu.illinois.cs.cogcomp.sl.core.AbstractFeatureGenerator;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.FeatureVectorBuffer;
import edu.illinois.cs.cogcomp.sl.util.IFeatureVector;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.io.Serializable;

/**
 * @author Bhargav Mangipudi
 */
public abstract class FeatureDefinitionBase extends AbstractFeatureGenerator implements Serializable {
    protected Lexiconer lexiconer;

    public FeatureDefinitionBase(Lexiconer lm) {
        this.lexiconer = lm;
    }

    // Assumes lexicon is populated already.
    public void preExtractFeatures(SLInstance instance) {
        int numOfLabels = this.lexiconer.getNumOfLabels();

        // Assumes are labels are indexed between 0 <= labelId < numLabels
        for (int i = 0; i < numOfLabels; i++) {
            String labelString = this.lexiconer.getLabelString(i);

            // Ideally this should be better.
            this.getFeatureVector(instance, new SLStructure(labelString));
        }
    }

    public abstract FeatureVectorBuffer getFeatureVector(SLInstance instance, SLStructure structure);

    public IFeatureVector getFeatureVector(IInstance x, IStructure y) {
        SLInstance ins = (SLInstance) x;
        SLStructure str = (SLStructure) y;

        return getFeatureVector(ins, str).toFeatureVector();
    }
}
