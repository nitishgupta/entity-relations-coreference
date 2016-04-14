package edu.illinois.cs.cogcomp.erc.corpus;

import edu.illinois.cs.cogcomp.erc.ir.Document;

import java.io.*;
import java.util.*;

/**
 * Created by nitishgupta on 2/21/16.
 */
public class Corpus implements Serializable{
    static final long serialVersionUID = 1L;

    private List<Document> docs;
    private boolean isACE04;
    public static final String NER_GOLD_HEAD_BIO_VIEW = "NER_GOLD_HEAD_BIO_VIEW";
    public static final String NER_GOLD_EXTENT_BIO_VIEW = "NER_GOLD_EXTENT_BIO_VIEW";
    public static final String NER_GOLD_EXTENT_SPAN = "NER_GOLD_EXTENT_SPAN";
    public static final String NER_GOLD_HEAD_SPAN = "NER_GOLD_HEAD_SPAN";
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
