package edu.illinois.cs.cogcomp.erc.sl.relations;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.PredicateArgumentView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Sentence;
import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;

/**
 * @author Bhargav Mangipudi
 */
public class SLHelper {
    public static int count = 0;

    public static SLProblem populateSLProblemForDocument(Document document) {
        SLProblem problem = new SLProblem();

        PredicateArgumentView relationView = (PredicateArgumentView) document.getTA().getView(ACEReader.RELATIONVIEW);

        int violations = 0;
        for (Constituent predicate : relationView.getPredicates()) {
            Constituent argument = predicate.getOutgoingRelations().get(0).getTarget();
            if (predicate.getSentenceId() != argument.getSentenceId()) {
                Sentence firstSen =  predicate.getTextAnnotation().getSentence(predicate.getSentenceId());
                Sentence secondSen = argument.getTextAnnotation().getSentence(argument.getSentenceId());

                System.out.println("First Sentence: " + firstSen.toString());
                System.out.println(predicate.toString());

                for (String key : predicate.getAttributeKeys()) {
                    if (!key.startsWith("Relation")) continue;
                    System.out.println(key + "-" + predicate.getAttribute(key));
                }

                System.out.println("Second Sentence: " + secondSen.toString());
                System.out.println(argument.toString());
                for (String key : argument.getAttributeKeys()) {
                    if (!key.startsWith("Relation")) continue;
                    System.out.println(key + "-" + argument.getAttribute(key));
                }

                System.out.println();

                violations++;
            }
        }

//        if (violations != 0) {
//            System.out.println("Number of violations = " + violations);
//        }

        count += violations;

        return problem;
    }
}
