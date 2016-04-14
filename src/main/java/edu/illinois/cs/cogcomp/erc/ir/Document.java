package edu.illinois.cs.cogcomp.erc.ir;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.util.PipelineService;

import java.io.Serializable;

/**
 * Created by Bhargav Mangipudi on 2/26/16.
 */
public class Document implements Serializable {
    static final long serialVersionUID = 6L;

    TextAnnotation ta;
    boolean is2004;
    String filename;

    public Document(TextAnnotation ta, boolean is2004, String filename) {
        this.ta = ta;
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

    public String getFilename(){
        return filename;
    }

    public TokenLabelView getNERExtentBIOView(){
        if (ta.hasView(Corpus.NER_GOLD_EXTENT_BIO_VIEW))
            return (TokenLabelView) this.getTA().getView(Corpus.NER_GOLD_EXTENT_BIO_VIEW);

        return null;
    }

    public TokenLabelView getNERHeadBIOView(){
        if (ta.hasView(Corpus.NER_GOLD_HEAD_BIO_VIEW))
            return (TokenLabelView) this.getTA().getView(Corpus.NER_GOLD_HEAD_BIO_VIEW);

        return null;
    }

    public View getSentenceView(){
        if (ta.hasView(ViewNames.SENTENCE))
            return this.getTA().getView(ViewNames.SENTENCE);

        return null;
    }
}
