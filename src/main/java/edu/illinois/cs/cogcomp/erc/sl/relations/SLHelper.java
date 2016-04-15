package edu.illinois.cs.cogcomp.erc.sl.relations;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;

import java.util.List;

/**
 * @author Bhargav Mangipudi
 */
public class SLHelper {
    public static List<Pair<SLInstance, SLStructure>> populateSLProblemForDocument(Document document, String relationViewName, Lexiconer lm) {
        SLProblem problem = new SLProblem();

        TextAnnotation ta = document.getTA();

        int numSentences = ta.getNumberOfSentences();
        Sentence s;
        for (int i = 0; i < numSentences; i++) {
            s = ta.getSentence(i);

            List<Constituent> relationItems = s.getView(relationViewName).getConstituentsCoveringSpan(s.getStartSpan(), s.getEndSpan());
        }

        return null;
    }
}
