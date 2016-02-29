package edu.illinois.cs.cogcomp.erc.util;

import edu.illinois.cs.cogcomp.erc.ir.Document;
import org.apache.commons.io.FileUtils;

import java.io.*;

/**
 * Created by Bhargav Mangipudi on 2/26/16.
 */
public class Utils {

    public static String getCorpusTypeFromFilename(String filename) {
        return filename.substring(0, filename.indexOf('/'));
    }

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
}
