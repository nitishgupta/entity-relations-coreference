package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;

import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;

import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitishgupta on 3/9/16.
 */
public class Test {



    public static void addNERView(Corpus testData, String viewName, String modelPath) throws Exception {
        System.out.println("Adding Predicted BIO View");
        SLModel model = SLModel.loadModel(modelPath);

        // Disable modification of lexicon while testing.
        model.lm.setAllowNewFeatures(false);

        for(Document doc : testData.getDocs()){
            TextAnnotation ta = doc.getTA();
            String outputViewName = null;
            TokenLabelView NER_PRED_BIO_VIEW = null;
            if(viewName.equals(Corpus.NER_GOLD_EXTENT_BIO_VIEW)) {
                outputViewName = Corpus.NER_PRED_EXTENT_BIO_VIEW;
                NER_PRED_BIO_VIEW = new TokenLabelView(outputViewName, "sl.ner.Test", ta, 1.0);
            }
            else if(viewName.equals(Corpus.NER_GOLD_HEAD_BIO_VIEW)) {
                outputViewName = Corpus.NER_PRED_HEAD_BIO_VIEW;
                NER_PRED_BIO_VIEW = new TokenLabelView(outputViewName, "sl.ner.Test", ta, 1.0);
            }

            else{
                System.out.println("View Name not found : " + viewName);
                System.exit(0);
            }

            View tokensview = ta.getView(Corpus.TOKENS_VIEW);
            List<Constituent> tokens = tokensview.getConstituents();
            View SentenceView = doc.getSentenceView();

            for(Constituent sentence : SentenceView.getConstituents()) {
                IInstance instance = MainClass.getIInstanceForSentence(sentence, model.lm);
                SequenceLabel prediction = (SequenceLabel) model.infSolver.getBestStructure(model.wv, instance);
                int start_token = sentence.getStartSpan();  // Inclusive
                int end_token = sentence.getEndSpan() - 1;  // Inclusive

                List<Constituent> token_constituents = new ArrayList<Constituent>();

                for(int token_num = start_token; token_num <= end_token; token_num++) {
                    Constituent c = tokens.get(token_num);
                    Constituent outC = c.cloneForNewViewWithDestinationLabel(outputViewName,
                            prediction.getLabelAtPosition(token_num - start_token));

                    NER_PRED_BIO_VIEW.addConstituent(outC);
                }
            }
            ta.addView(outputViewName, NER_PRED_BIO_VIEW);
        }
        System.out.println("Predicted BIO View Added");
    }

    public static void testNER(Corpus testData, String goldViewName) throws Exception {
        TestDiscrete testDiscreteFormatted = new TestDiscrete();
        TestDiscrete testFiltered = new TestDiscrete();
        testFiltered.addNull("O");
        for(Document doc : testData.getDocs()) {
            TextAnnotation ta = doc.getTA();
            String predViewName = null;
            View goldView = ta.getView(goldViewName);
            View predView = null;
            if (goldViewName.equals(Corpus.NER_GOLD_EXTENT_BIO_VIEW)) {
                predViewName = Corpus.NER_PRED_EXTENT_BIO_VIEW;
                predView = ta.getView(predViewName);
            } else if (goldViewName.equals(Corpus.NER_GOLD_HEAD_BIO_VIEW)) {
                predViewName = Corpus.NER_PRED_HEAD_BIO_VIEW;
                predView = ta.getView(predViewName);
            } else {
                System.out.println("View Name not found : " + goldViewName);
                System.exit(0);
            }

            List<Constituent> predCons = predView.getConstituents();
            List<Constituent> goldCons = goldView.getConstituents();


            for (int j = 0; j < predCons.size(); j++) {
                testDiscreteFormatted.reportPrediction(
                        predCons.get(j).getLabel(),
                        goldCons.get(j).getLabel());

                testFiltered.reportPrediction(
                        predCons.get(j).getLabel(),
                        goldCons.get(j).getLabel());
            }
        }

        System.out.println("Formatted Performance Metrics");
        testDiscreteFormatted.printPerformance(System.out);

        System.out.println("Filtered Performance Metrics");
        testFiltered.printPerformance(System.out);
    }
}
