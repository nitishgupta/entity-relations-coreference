package edu.illinois.cs.cogcomp.erc.ir;

import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.util.PipelineService;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocumentAnnotation;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEEntity;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEEntityMention;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 2/26/16.
 */
public class Document implements Serializable {
    static final long serialVersionUID = 6L;

    TextAnnotation ta;
    ACEDocumentAnnotation aceAnnotation;
    String contentRemovingTags;
    boolean is2004;
    String filename;



    public Document(TextAnnotation ta, ACEDocumentAnnotation aceAnnotation, String contentRemovingTags, boolean is2004, String filename) {
        this.ta = ta;
        this.aceAnnotation = aceAnnotation;
        this.contentRemovingTags = contentRemovingTags;
        this.is2004 = is2004;
        this.filename = filename;

        // Add Pipeline Views as well
        this.addPipeLineViews();
    }

    public Document(ACEDocumentAnnotation aceAnnotation, String contentRemovingTags, boolean is2004, String filename) {
        TextAnnotationBuilder taBuilder = new CcgTextAnnotationBuilder(new IllinoisTokenizer());
        if(is2004)
            ta = taBuilder.createTextAnnotation( "ACE2004", filename, contentRemovingTags);
        else
            ta = taBuilder.createTextAnnotation( "ACE2005", filename, contentRemovingTags);
        this.aceAnnotation = aceAnnotation;
        this.contentRemovingTags = contentRemovingTags;
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

    public String getContentRemovingTags(){ return contentRemovingTags; }

    public List<ACEEntityMention> getGoldMentions(){
        List<ACEEntityMention> mentions = new ArrayList<ACEEntityMention>();
        List<ACEEntity> entityList = aceAnnotation.entityList;

        for(ACEEntity entity : entityList){
            mentions.addAll(entity.entityMentionList);
        }
        return mentions;
    }
}
