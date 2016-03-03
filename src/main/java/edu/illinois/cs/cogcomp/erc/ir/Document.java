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
    List<TextAnnotation> taList;
    ACEDocumentAnnotation aceAnnotation;
    boolean is2004;
    String filename;

    public Document(List<TextAnnotation> taList, ACEDocumentAnnotation aceAnnotation, boolean is2004, String filename) {
        this.taList = taList;
        this.aceAnnotation = aceAnnotation;
        this.is2004 = is2004;
        this.filename = filename;

        // Add Pipeline Views as well
        this.addPipeLineViews();
    }

    public void addPipeLineViews() {
        for (TextAnnotation ta : this.taList) {
            pipeline.addShallowParse(ta);
            pipeline.addPOS(ta);
        }
    }

    public List<TextAnnotation> getTA() {
        return this.taList;
    }

    public ACEDocumentAnnotation getAceAnnotation() {
        return this.aceAnnotation;
    }
}
