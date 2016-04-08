package edu.illinois.cs.cogcomp.erc.reader;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.ir.DocUtils;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.erc.util.Utils;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEDocument;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.AceFileProcessor;
import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.ReadACEAnnotation;
import edu.illinois.cs.cogcomp.reader.commondatastructure.AnnotatedText;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhargav Mangipudi on 2/25/16.
 */
public abstract class DocumentReader {
    protected String baseDir;
    protected String filelistPath;
    protected String serializedDocDir;
    protected boolean is2004;

    protected static AceFileProcessor fileProcessor = new AceFileProcessor(new TokenizerTextAnnotationBuilder(new IllinoisTokenizer()));

    public Document readDocument(String fileName) {
        String cacheFileName = Utils.getCacheFilenameForDocument(fileName);

        // If cached file exists, try to read it first!
        if (new File(serializedDocDir + cacheFileName).exists()) {
            Document cachedDocument = Utils.readSerializedDocument(serializedDocDir, cacheFileName);
            if (cachedDocument != null) return cachedDocument;
        }

        // Read document from dataset
        ReadACEAnnotation.is2004mode = this.is2004;

        // Extract nw from nw/filename.apf.xml
        String prefix = Utils.getCorpusTypeFromFilename(fileName);
        String fullfilepath = this.baseDir + fileName;

        if (!new File(fullfilepath).exists())
            System.out.println("Document File not found! - " + fullfilepath);

        Document newDoc = null;
        try {
            ACEDocument doc = fileProcessor.processAceEntry(new File(this.baseDir + prefix + "/"), fullfilepath);
            String contentRemovingTags = doc.contentRemovingTags;

            newDoc = new Document(doc.aceAnnotation, contentRemovingTags, is2004, fileName);

            // ADDING SPAN NER EXTENT VIEW IN TA
            if(!newDoc.getTA().hasView(Corpus.NER_GOLD_EXTENT_SPAN))
                DocUtils.createGOLDNER_ExtentView(newDoc);
            if(!newDoc.getTA().hasView(Corpus.NER_GOLD_HEAD_SPAN))
                DocUtils.createGOLDNER_HeadView(newDoc);
            // ADDING BIO NER EXTENT VIEW IN TA
            if(!newDoc.getTA().hasView(Corpus.NER_GOLD_HEAD_BIO_VIEW))
                DocUtils.addNERHeadBIOView(newDoc);
            if(!newDoc.getTA().hasView(Corpus.NER_GOLD_EXTENT_BIO_VIEW))
                DocUtils.addNERExtentBIOView(newDoc);

            // Write document to cache
            Utils.writeSerializedDocument(newDoc, serializedDocDir, cacheFileName);
        } catch (Exception ex) {
            if (Parameters.isDebug) System.err.println("Failed to parse document - " + fullfilepath);
            if (Parameters.isDebug) ex.printStackTrace(System.err);
        }

        return newDoc;
    }

    /**
     * filelistPath - filenames are as folder/filename - eg. nw/filename.xml
     *
     * @return
     */
    public List<Document> readCorpus() {
        try {
            List<Document> documents = new ArrayList<>();
            for (String file : LineIO.read(this.filelistPath)) {
                Document doc = readDocument(file);

                if (doc != null) documents.add(doc);
            }

            return documents;
        } catch (FileNotFoundException ex) {
            if (Parameters.isDebug) System.err.println("Cannot find file list! at " + this.filelistPath);
        }

        return null;
    }

    public static void readDocumentTester(String directory, String filename){
        AceFileProcessor fileProcessor = new AceFileProcessor(new TokenizerTextAnnotationBuilder(new IllinoisTokenizer()));
        ReadACEAnnotation.is2004mode = true;

        ACEDocument doc = fileProcessor.processAceEntry(new File(directory), filename);
        List<AnnotatedText> ATList = doc.taList;
        System.out.println("ATList : " + ATList.size());

        System.out.println("content : " + doc.orginalContent);
        System.out.println("Entities : " + doc.aceAnnotation.entityList.size());
        System.out.println("Relations : " + doc.aceAnnotation.relationList.size());
        System.out.println("timeEx : " + doc.aceAnnotation.timeExList.size());
        System.out.println("paraList : " + doc.paragraphs);



        List<TextAnnotation> taList = AceFileProcessor.populateTextAnnotation(doc);
        System.out.println("Number of TAS : " + taList.size());


    }
}
