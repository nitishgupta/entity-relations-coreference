package edu.illinois.cs.cogcomp.erc.sl.relations;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

/**
 * @author Bhargav Mangipudi
 */
public class SLInstance implements IInstance {
    private Constituent firstMention;
    private Constituent secondMention;

    public SLInstance(Constituent firstMention, Constituent secondMention) {
        assert firstMention != null;
        assert secondMention != null;

        this.firstMention = firstMention;
        this.secondMention = secondMention;
    }

    public Constituent getFirstMention() {
        return firstMention;
    }

    public Constituent getSecondMention() {
        return secondMention;
    }
}
