package edu.illinois.cs.cogcomp.ace04;


/**
 * Created by nitishgupta on 2/19/16.
 */
public class reader {
    private static final String TEST_DIR="src/test/resources/examples/nw";
    private static final String TEST_FILE="XIN_ENG_20030616.0274.apf.xml";


    public void testProcessDocument()
    {
        AceFileProcessor proc = new AceFileProcessor( new CcgTextAnnotationBuilder( new IllinoisTokenizer() ) );

        ACEDocument doc = proc.processAceEntry(new File(TEST_DIR), TEST_DIR + "/" + TEST_FILE);

        List<TextAnnotation> taList = AceFileProcessor.populateTextAnnotation(doc);

        for ( TextAnnotation ta : taList )
        {
            assertTrue( ta.hasView(EventConstants.NER_ACE ) );
        }
    }
}
