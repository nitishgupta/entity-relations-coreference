package edu.illinois.cs.cogcomp.erc.ir;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.util.PipelineService;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocumentAnnotation;

import java.io.Serializable;

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
        PipelineService.addShallowParse(ta);
        PipelineService.addPOS(ta);
    }

    public TextAnnotation getTA() {
        return this.ta;
    }

    public ACEDocumentAnnotation getAceAnnotation() {
        return this.aceAnnotation;
    }

    public String getFilename(){
        return filename;
    }

    public TokenLabelView getNERBIOView(){
        if (ta.hasView(Corpus.NER_GOLD_BIO_VIEW))
            return (TokenLabelView) this.getTA().getView(Corpus.NER_GOLD_BIO_VIEW);

        return null;
    }

    public View getSentenceView(){
        if (ta.hasView(ViewNames.SENTENCE))
            return this.getTA().getView(ViewNames.SENTENCE);

        return null;
    }

}
