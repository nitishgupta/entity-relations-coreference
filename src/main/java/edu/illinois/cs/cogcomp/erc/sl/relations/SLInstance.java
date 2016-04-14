package edu.illinois.cs.cogcomp.erc.sl.relations;

import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

import java.util.Arrays;

/**
 * @author Bhargav Mangipudi
 */
public class SLInstance implements IInstance {
    private Constituent firstMention;

    private Constituent secondMention;

    private TextAnnotation document;

    public SLInstance(Constituent firstMention, Constituent secondMention) {
        this.firstMention = firstMention.cloneForNewView(ACEReader.RELATIONVIEW);
        this.secondMention = secondMention.cloneForNewView(ACEReader.RELATIONVIEW);

        // Clean-up some labels et. al.
        for (Constituent c : Arrays.asList(firstMention, secondMention)) {
            c.addAttribute(ACEReader.RelationIDAttribute, "");
        }
    }

    public Constituent getFirstMention() { return this.firstMention; }

    public Constituent getSecondMention() { return this.secondMention; }
}
