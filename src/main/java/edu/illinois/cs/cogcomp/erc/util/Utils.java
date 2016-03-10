package edu.illinois.cs.cogcomp.erc.util;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.core.utilities.StringUtils;
import edu.illinois.cs.cogcomp.erc.config.Parameters;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

/**
 * Created by Bhargav Mangipudi on 2/26/16.
 */
public class Utils {

    /**
     * @param filename : filename from filelist - eg. nw/filename.apf.xml
     * @return corpustype folder name : eg nw
     */
    public static String getCorpusTypeFromFilename(String filename) {
        return filename.substring(0, filename.indexOf('/'));
    }

    /**
     *
     * @param filename : filename from filelist - eg. nw/filename.apf.xml
     * @return serializedFileName : eg. nw_filename.ser
     */
    public static String getCacheFilenameForDocument(String filename) {
        return filename.replace("/", "_").substring(0, filename.indexOf(".apf.xml")) + ".ser";
    }

    public static void writeSerializedDocument(Document doc, String dir, String cacheFileName) {
        File directory = new File(dir);
        try {
            FileUtils.forceMkdir(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String pathToWrite = dir + cacheFileName;

        try (
                OutputStream file = new FileOutputStream(pathToWrite);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer);
        ) {
            output.writeObject(doc);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.err.println("Cannot Write Document at " + cacheFileName);
        }
    }

    public static Document readSerializedDocument(String dir, String cacheFileName) {
        String pathToRead = dir + cacheFileName;
        try (
                InputStream file = new FileInputStream(pathToRead);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput output = new ObjectInputStream(buffer);
        ) {
            return ((Document) output.readObject());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.err.println("Cannot Write Document at " + cacheFileName);
        }

        return null;
    }

    public static Corpus readSerializedCorpus(String corpuspath) {
        String pathToRead = corpuspath;
        try (
                InputStream file = new FileInputStream(pathToRead);
                InputStream buffer = new BufferedInputStream(file);
                ObjectInput output = new ObjectInputStream(buffer);
        ) {
            return ((Corpus) output.readObject());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.err.println("Cannot Read Corpus : " + pathToRead);
        }

        return null;
    }

    public static void writeSerializedCorpus(Corpus c, String pathToWrite) {
        try (
                OutputStream file = new FileOutputStream(pathToWrite);
                OutputStream buffer = new BufferedOutputStream(file);
                ObjectOutput output = new ObjectOutputStream(buffer);
        ) {
            output.writeObject(c);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.err.println("Cannot Write Corpus at " + pathToWrite);
        }
    }

    public static void countCorpusTypeDocs(Corpus corpus){
        List<Document> docs = corpus.getDocs();
        Map<String, Integer> corpusTypeCount = new HashMap<String, Integer>();

        for(Document doc : docs){
            String filename = doc.getFilename();
            String corpusType = Utils.getCorpusTypeFromFilename(filename);

            if(!corpusTypeCount.containsKey(corpusType))
                corpusTypeCount.put(corpusType, 0);

            int count = corpusTypeCount.get(corpusType);
            corpusTypeCount.put(corpusType, count+1);
        }

        for(String corpusType : corpusTypeCount.keySet())
            System.out.println(corpusType + " : " + corpusTypeCount.get(corpusType));

    }

    public static void printTAConstitutents(List<Constituent> constituents){
        for(Constituent c : constituents){
            System.out.print(c.getSurfaceForm() + ":"+c.getStartSpan()+":"+c.getEndSpan()+":"+c.getLabel()+" ");
        }
        System.out.println("\n");
    }


}
