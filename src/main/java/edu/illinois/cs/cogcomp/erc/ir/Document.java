package edu.illinois.cs.cogcomp.erc.ir;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocumentAnnotation;

/**
 * Created by Bhargav Mangipudi on 2/26/16.
 */
public class Document {
    TextAnnotation ta;
    ACEDocumentAnnotation aceAnnotation;

    public Document(TextAnnotation ta, ACEDocumentAnnotation aceAnnotation) {
        this.ta = ta;
        this.aceAnnotation = aceAnnotation;
    }

    public TextAnnotation getTA() {
        return this.ta;
    }

    public ACEDocumentAnnotation getAceAnnotation() {
        return this.aceAnnotation;
    }
}
