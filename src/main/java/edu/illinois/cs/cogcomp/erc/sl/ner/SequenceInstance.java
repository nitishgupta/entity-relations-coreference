package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.sl.core.IInstance;

import java.util.Collections;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 3/7/16.
 *
 * The sequence instance is one input sentence. For NER it is a list of constituents from the NER_GOLD_BIO_VIEW for a sentence
 * Hence from each part of the sequence (token) the surface form (along with other data) can be extracted
 * TODO : Think about how to encode POS data in this. One way is to make a similar list of constituents from the POS view.
 * OR to save space, we can make a same sized list of POS tags for the sentence.
 *
 *
 */
public class SequenceInstance implements IInstance {

    List<Constituent> constituents;
    final int hashCode;

    public SequenceInstance(List<Constituent> constituentList) {
        assert constituentList.size() != 0;
        this.constituents = Collections.unmodifiableList(constituentList);

        int hashCodeVal = 0;
        for (Constituent c : this.constituents) {
            hashCodeVal += c.hashCode()*19; // Verify
        }

        this.hashCode = hashCodeVal;
    }

    public List<Constituent> getConstituents() { return this.constituents; }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
