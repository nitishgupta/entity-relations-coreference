package edu.illinois.cs.cogcomp.erc.ir;

import edu.illinois.cs.cogcomp.core.datastructures.ViewNames;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TokenLabelView;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.erc.corpus.Corpus;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEEntity;
import edu.illinois.cs.cogcomp.reader.ace2005.annotationStructure.ACEEntityMention;
import edu.illinois.cs.cogcomp.reader.util.EventConstants;

import java.util.*;

/**
 * Created by nitishgupta on 3/7/16.
 */
public class DocUtils {

    private static final String NAME = DocUtils.class.getCanonicalName();

    public static void addNERCoarseBIOView(Document doc) {
        DocUtils.addBIOView(doc, Corpus.NER_GOLD_COARSE_VIEW, Corpus.NER_GOLD_BIO_VIEW);
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



    public static void createGOLDNER_ExtentView(Document doc){
        TextAnnotation ta = doc.getTA();
        String viewName = Corpus.NER_GOLD_COARSE_EXTENT;

        List<ACEEntity> entities = doc.getAceAnnotation().entityList;
        List<Constituent> coarse_constituents = new ArrayList<Constituent>();

        for ( ACEEntity e : entities ) {
            String type = e.type;
            for(ACEEntityMention mention : e.entityMentionList) {
                int startCharOffset = mention.extentStart;
                int endCharOffset = mention.extentEnd;
                int start_token = ta.getTokenIdFromCharacterOffset(startCharOffset);
                int end_token = ta.getTokenIdFromCharacterOffset(endCharOffset);

                if(start_token >= 0 && end_token >= 0 && !(end_token-start_token < 0)){
                    // Be careful with the +1 in end_span below. Regular TextAnnotation likes the end_token number exclusive
                    Constituent c = new Constituent(type, 1.0, viewName, ta, start_token, end_token + 1);
                    coarse_constituents.add(c);
                }
            }
        }

        coarse_constituents = removeOverlappingEntities(coarse_constituents);
        View nerView = new View(viewName, NAME, ta, 1.0 );
        for(Constituent c : coarse_constituents)
            nerView.addConstituent(c);

        ta.addView(viewName, nerView);

    }

    public static List< Constituent > removeOverlappingEntities(List< Constituent > neConstituents ) {

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

        Set< Constituent > nesToRemove = new HashSet< Constituent >();

        int lastNeEnd = -1;
        Constituent prevNe = null;

        for ( Constituent ne : neConstituents ) {
            if (ne.getStartSpan() < lastNeEnd) {
                nesToRemove.add(prevNe);
            }
            lastNeEnd = ne.getEndSpan();
            prevNe = ne;
        }

        for ( Constituent e : nesToRemove )
            neConstituents.remove( e );

        return neConstituents;
    }

}
