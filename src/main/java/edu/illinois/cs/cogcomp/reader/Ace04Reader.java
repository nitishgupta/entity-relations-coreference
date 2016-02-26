package edu.illinois.cs.cogcomp.reader;

import edu.illinois.cs.cogcomp.reader.ace2005.documentReader.ReadACEAnnotation;

/**
 * Created by nitishgupta on 2/25/16.
 */
public class Ace04Reader extends DocumentReader {

    public static final String TEST_DIR="data/ace04/data/English/nw/";
    public static final String TEST_FILE="APW20001211.1441.0436.apf.xml";

    @Override
    public void testProcessDocument(String TEST_DIR, String TEST_FILE)
    {
        ReadACEAnnotation.is2004mode = true;
        super.testProcessDocument(TEST_DIR, TEST_FILE);
    }
}
