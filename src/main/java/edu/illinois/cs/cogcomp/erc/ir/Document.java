package edu.illinois.cs.cogcomp.erc.ir;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.features.pipeline;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocumentAnnotation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 2/26/16.
 */
public class Document implements Serializable {
    static final long serialVersionUID = 6L;

    TextAnnotation ta;
    ACEDocumentAnnotation aceAnnotation;
    boolean is2004;
    String filename;

    public Document(TextAnnotation ta, ACEDocumentAnnotation aceAnnotation, boolean is2004, String filename) {
        this.ta = ta;
        this.aceAnnotation = aceAnnotation;
        this.is2004 = is2004;
        this.filename = filename;

        // Add Pipeline Views as well
        this.addPipeLineViews();
    }

    public void addPipeLineViews() {
        pipeline.addShallowParse(ta);
        pipeline.addPOS(ta);
    }

    public TextAnnotation getTA() {
        return this.ta;
    }

    public ACEDocumentAnnotation getAceAnnotation() {
        return this.aceAnnotation;
    }
}
