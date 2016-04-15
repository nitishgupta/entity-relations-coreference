package edu.illinois.cs.cogcomp.erc.sl.relations;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

/**
 * @author Bhargav Mangipudi
 */
public class SLInstance implements IInstance {
    private Sentence sentence;

    public SLInstance(Sentence sentence) {
        assert sentence != null;
        assert sentence.getStartSpan() < sentence.getEndSpan();

        this.sentence = sentence;
    }
}
