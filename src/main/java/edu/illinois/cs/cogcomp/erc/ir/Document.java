package edu.illinois.cs.cogcomp.erc.ir;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.features.pipeline;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocumentAnnotation;

import java.io.Serializable;

/**
 * Created by Bhargav Mangipudi on 2/26/16.
 */
public class Document implements Serializable {
    TextAnnotation ta;
    ACEDocumentAnnotation aceAnnotation;
    boolean is2004;
    String filename;

    public Document(TextAnnotation ta, ACEDocumentAnnotation aceAnnotation, boolean is2004, String filename) {
        this.ta = ta;
        this.aceAnnotation = aceAnnotation;
        this.is2004 = is2004;
        this.filename = filename;
    }

    public void addPipeLineViews(){
        pipeline.addShallowParse(this.ta);
        pipeline.addPOS(this.ta);
    }

    public TextAnnotation getTA() {
        return this.ta;
    }

    public ACEDocumentAnnotation getAceAnnotation() {
        return this.aceAnnotation;
    }
}
