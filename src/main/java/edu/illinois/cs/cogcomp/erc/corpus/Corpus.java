package edu.illinois.cs.cogcomp.erc.corpus;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by nitishgupta on 2/21/16.
 */
public class Corpus implements Serializable{
    static final long serialVersionUID = 1L;

    List<Document> docs;
    boolean isACE04;
    //Map<ACEDocument, TextAnnotation> docs;

    public Corpus(List<Document> docs, boolean isACE04){
        this.docs = docs;
        this.isACE04 = isACE04;
    }

    public List<Document> getDocs(){
        if(docs!=null)
            return docs;
        else {
            System.out.println("List of Documents is NULL");
            return null;
        }
    }

    public boolean checkisACE2004(){
        return isACE04;
    }





}
