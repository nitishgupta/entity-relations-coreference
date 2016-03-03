package edu.illinois.cs.cogcomp.erc.reader;

import edu.illinois.cs.cogcomp.erc.config.Parameters;

/**
 * Created by nitishgupta on 2/25/16.
 */
public class Ace04Reader extends DocumentReader {

    public Ace04Reader() {
        super();

        this.is2004 = true;
        this.baseDir = Parameters.ACE04_DATA_DIR;
        this.filelistPath = Parameters.ACE04_FILELIST;
        this.serializedDocDir = Parameters.ACE04_SERIALIZED_DOCS;
    }
}
