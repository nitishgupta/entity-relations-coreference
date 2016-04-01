package edu.illinois.cs.cogcomp.erc.corpus;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;

import java.io.*;
import java.util.*;

/**
 * Created by nitishgupta on 2/21/16.
 */
public class Corpus implements Serializable{
    static final long serialVersionUID = 1L;

    private List<Document> docs;
    private boolean isACE04;
    public static final String NER_GOLD_COARSE_VIEW = "NER_ACE_COARSE";
    public static final String NER_GOLD_BIO_VIEW = "NER_GOLD_BIO_VIEW";
    public static final String NER_GOLD_COARSE_EXTENT = "NER_GOLD_COARSE_EXTENT";
    public static final String NER_GOLD_COARSE_HEAD = "NER_GOLD_COARSE_HEAD";
    public static final String TOKENS_VIEW = "TOKENS";
    public static final String SENTENCE_VIEW = "SENTENCE";
    public static final String SHALLOW_PARSE_VIEW = "SHALLOW_PARSE";
    public static final String POS_VIEW = "POS";


    /*
    Have included a method to shuffle docs when storing because
        0. The docs are read in the order of the folder. When training want to present docs of different folders in a random fashion
        1. Do not want to shuffle everytime when returning.
     */
    public Corpus(List<Document> docs, boolean isACE04){
        //Collections.shuffle(docs);
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

    public Document getDoc(int index){
        if(index >= docs.size()){
            System.err.println("Given doc index out of bounds : " + index);
            System.exit(0);
        }
        return docs.get(index);
    }

    public boolean checkisACE2004(){
        return isACE04;
    }

    public int numDocs(){   return docs.size(); }
}
