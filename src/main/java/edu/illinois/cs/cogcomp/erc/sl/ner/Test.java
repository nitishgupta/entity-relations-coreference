package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;

import edu.illinois.cs.cogcomp.erc.ir.Document;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;

import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.SLModel;
import edu.illinois.cs.cogcomp.sl.core.SLProblem;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitishgupta on 3/9/16.
 */
public class Test {
    public static void addNERView(Document doc, String outputViewName, SLModel model) throws Exception {
        // Disable modification of lexicon while testing.
        model.lm.setAllowNewFeatures(false);

        TextAnnotation ta = doc.getTA();
        View tokensview = ta.getView(Corpus.TOKENS_VIEW);
        TokenLabelView NER_PRED_BIO_VIEW = new TokenLabelView(outputViewName, "sl.ner.Test", ta, 1.0);

        List<Constituent> tokens = tokensview.getConstituents();
        View SentenceView = doc.getSentenceView();

        for(Constituent sentence : SentenceView.getConstituents()) {
            IInstance instance = MainClass.getIInstanceForSentence(sentence, model.lm);
            SequenceLabel prediction = (SequenceLabel) model.infSolver.getBestStructure(model.wv, instance);
            int start_token = sentence.getStartSpan();  // Inclusive
            int end_token = sentence.getEndSpan() - 1;  // Inclusive

            for(int token_num = start_token; token_num <= end_token; token_num++) {
                Constituent c = tokens.get(token_num);
                Constituent outC = c.cloneForNewViewWithDestinationLabel(outputViewName,
                        prediction.getLabelAtPosition(token_num - start_token));

                NER_PRED_BIO_VIEW.addConstituent(outC);
            }
        }
        ta.addView(outputViewName, NER_PRED_BIO_VIEW);
    }

    public static void addNERView(Corpus testData, String viewName, String modelPath) throws Exception {
        System.out.println("Adding Predicted BIO View");

        String outputViewName = null;

        if(viewName.equals(Corpus.NER_GOLD_EXTENT_BIO_VIEW)) {
            outputViewName = Corpus.NER_PRED_EXTENT_BIO_VIEW;
        }
        else if(viewName.equals(Corpus.NER_GOLD_HEAD_BIO_VIEW)) {
            outputViewName = Corpus.NER_PRED_HEAD_BIO_VIEW;
        }
        else{
            System.out.println("View Name not found : " + viewName);
            System.exit(0);
        }

        SLModel slModel = SLModel.loadModel(modelPath);
        for(Document doc : testData.getDocs()) {
            Test.addNERView(doc, outputViewName, slModel);
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

    public static void writeNEROutput(Corpus corpus, String nerViewName, String outputfilepath){
        StringBuilder output = new StringBuilder();

        for(Document d : corpus.getDocs()){
            TextAnnotation ta = d.getTA();
            String id = ta.getId();
            SpanLabelView view = (SpanLabelView) ta.getView(nerViewName);
            List<Constituent> constituents = view.getConstituents();
            for(Constituent c : constituents) {
                String out = id + "\t";   // DOCID
                int start = c.getStartSpan();
                int end = c.getEndSpan();
                out += start + "\t";         // SPAN START
                out += end + "\t";         // SPAN END
                out += "NIL" + id + "-" + c.getLabel()+ "\t";               // CLUSTER ID
                out += "1.0" + "\t";                            // SCORE
                out += c.getLabel() + "\n";                     // NER TYPE
                output.append(out);
            }
        }

        String output_string = output.toString();
        try {
            FileUtils.writeStringToFile(new File(outputfilepath), output_string);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Solution Written to File - Succesfully");
    }
}
