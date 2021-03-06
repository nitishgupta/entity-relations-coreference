package edu.illinois.cs.cogcomp.erc.ir;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.*;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.nlp.corpusreaders.ACEReader;

import java.util.*;

/**
 * Created by nitishgupta on 3/7/16.
 */
public class DocUtils {

    private static final String NAME = DocUtils.class.getCanonicalName();

    public static void addNERHeadBIOView(Document doc) {
        DocUtils.addBIOView(doc, Corpus.NER_GOLD_HEAD_SPAN, Corpus.NER_GOLD_HEAD_BIO_VIEW);
    }

    public static void addNERExtentBIOView(Document doc) {
        DocUtils.addBIOView(doc, Corpus.NER_GOLD_EXTENT_SPAN, Corpus.NER_GOLD_EXTENT_BIO_VIEW);
    }

    /**
     * Adds a BIO (Begin, Inside, Outside) representation view for an input SpanLabel View.
     * @param doc Document to be annotated.
     * @param inputViewName Name of the source view. (eg. NER_ACE_COARSE)
     * @param outputViewName Name for the output view. (eg. NER_GOLD_COARSE_BIO)
     */
    public static void addBIOView(Document doc, String inputViewName, String outputViewName){
        TextAnnotation ta = doc.getTA();

        View inputView = ta.getView(inputViewName);

        View tokens = ta.getView(Corpus.TOKENS_VIEW);
        View bioView = new TokenLabelView(outputViewName, DocUtils.class.getCanonicalName(), ta, 1.0D);

        Map<Integer, Integer> start_end = new HashMap<Integer, Integer>();
        for (Constituent c : inputView.getConstituents()) {
            start_end.put(c.getStartSpan(), c.getEndSpan() - 1);
        }

        List<Constituent> tokens_constituents = tokens.getConstituents();

        int i = 0;
        while (i < tokens_constituents.size()) {
            Constituent token = tokens_constituents.get(i);
            Constituent c_bio;

            // Either token i is start of NER type or OUTSIDE
            if(start_end.containsKey(i)) {
                int end = start_end.get(i);
                List<Constituent> cons = inputView.getConstituentsCoveringToken(i);
                String label = cons.get(0).getLabel();

                c_bio = token.cloneForNewViewWithDestinationLabel(outputViewName, "B-"+label);
                bioView.addConstituent(c_bio);
                i++;

                while (i <= end) {
                    token = tokens_constituents.get(i);
                    c_bio = token.cloneForNewViewWithDestinationLabel(outputViewName, "I-" + label);
                    bioView.addConstituent(c_bio);
                    i++;
                }
            }
            else {
                c_bio = token.cloneForNewViewWithDestinationLabel(outputViewName, "O");
                bioView.addConstituent(c_bio);
                i++;
            }
        }

        ta.addView(outputViewName, bioView);
    }

    public static void createGOLDNER_ExtentView(Document doc) {
        TextAnnotation ta = doc.getTA();
        String viewName = Corpus.NER_GOLD_EXTENT_SPAN;
        List<Constituent> constituentList = new ArrayList<>();

        SpanLabelView entityView = (SpanLabelView) ta.getView(ViewNames.NER_ACE_COARSE_EXTENT);
        for (Constituent c : entityView.getConstituents()) {
            Constituent cons = c.cloneForNewView(viewName);
            constituentList.add(cons);
        }

        View nerView = new View(viewName, NAME, ta, 1.0 );
        for (Constituent filtered : removeOverlappingEntities(constituentList)) {
            nerView.addConstituent(filtered);
        }

        ta.addView(viewName, nerView);
    }

    public static void createGOLDNER_HeadView(Document doc) {
        TextAnnotation ta = doc.getTA();
        String viewName = Corpus.NER_GOLD_HEAD_SPAN;
        List<Constituent> constituentList = new ArrayList<>();

        SpanLabelView entityView = (SpanLabelView) ta.getView(ViewNames.NER_ACE_COARSE_HEAD);
        for (Constituent c : entityView.getConstituents()) {
            Constituent cons = DocUtils.getHeadConstituentForEntityExtent(c, viewName);
            if (cons != null) {
                constituentList.add(cons);
            }
        }

        View nerView = new View(viewName, NAME, ta, 1.0 );
        for (Constituent filtered : removeOverlappingEntities(constituentList)) {
            nerView.addConstituent(filtered);
        }

        ta.addView(viewName, nerView);
    }

    public static List<Constituent> removeOverlappingEntities(List<Constituent> neConstituents) {

        Collections.sort(neConstituents, new Comparator<Constituent>() {
            @Override
            public int compare(Constituent ca, Constituent cb) {
                if (ca.getStartSpan() < cb.getStartSpan())
                    return -1;
                else if (ca.getStartSpan() > cb.getStartSpan())
                    return 1;
                else if (ca.getEndSpan() > cb.getEndSpan())
                    return -1;
                else if (ca.getEndSpan() < cb.getEndSpan())
                    return 1;
                else
                    return 0;
            }
        });

        Set<Constituent> nesToRemove = new HashSet<>();

        int lastNeEnd = -1;
        Constituent prevNe = null;

        for (Constituent ne : neConstituents) {
            if (ne.getStartSpan() < lastNeEnd) {
                nesToRemove.add(prevNe);
            }

            lastNeEnd = ne.getEndSpan();
            prevNe = ne;
        }

        for (Constituent e : nesToRemove)
            neConstituents.remove(e);

        return neConstituents;
    }

    public static Constituent getHeadConstituentForEntityExtent(Constituent extent, String viewName) {
        TextAnnotation ta = extent.getTextAnnotation();

        int startCharOffset = Integer.parseInt(extent.getAttribute(ACEReader.EntityHeadStartCharOffset));
        int endCharOffset = Integer.parseInt(extent.getAttribute(ACEReader.EntityHeadEndCharOffset));
        int start_token = ta.getTokenIdFromCharacterOffset(startCharOffset);
        int end_token = ta.getTokenIdFromCharacterOffset(endCharOffset);

        if (start_token >= 0 && end_token >= 0 && !(end_token - start_token < 0)) {
            // Be careful with the +1 in end_span below. Regular TextAnnotation likes the end_token number exclusive
            Constituent cons = new Constituent(extent.getLabel(), 1.0, viewName, ta, start_token, end_token + 1);

            for (String attributeKey : extent.getAttributeKeys()) {
                cons.addAttribute(attributeKey, extent.getAttribute(attributeKey));
            }

            return cons;
        }

        return null;
    }
}
