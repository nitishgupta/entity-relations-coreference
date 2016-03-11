/*******************************************************************************
 * University of Illinois/NCSA Open Source License
 * Copyright (c) 2010, 
 *
 * Developed by:
 * The Cognitive Computations Group
 * University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal with the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimers.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimers in the documentation and/or other materials provided with the distribution.
 * Neither the names of the Cognitive Computations Group, nor the University of Illinois at Urbana-Champaign, nor the names of its contributors may be used to endorse or promote products derived from this Software without specific prior written permission.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
 *     
 *******************************************************************************/
package edu.illinois.cs.cogcomp.erc.sl.ner;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.sl.core.AbstractInferenceSolver;
import edu.illinois.cs.cogcomp.sl.core.IInstance;
import edu.illinois.cs.cogcomp.sl.core.IStructure;
import edu.illinois.cs.cogcomp.sl.util.Lexiconer;
import edu.illinois.cs.cogcomp.sl.util.WeightVector;

/**
 * An implementation of the Viterbi algorithm
 * @author kchang10
 */
public class ViterbiInferenceSolver extends
		AbstractInferenceSolver {

	private static final long serialVersionUID = 1L;	
	protected Lexiconer lm = null;
    protected FeatureGenerator featureGenerator;

	public ViterbiInferenceSolver(Lexiconer lm, FeatureGenerator fg) {
        this.lm = lm;
        this.featureGenerator = fg;
	}
	
	@Override
	public IStructure getLossAugmentedBestStructure(
			WeightVector wv, IInstance input, IStructure gold)
			throws Exception {
		assert !lm.isAllowNewFeatures() : "Lexiconer must be unmodifiable during inference.";

        // initialization
		SequenceLabel goldLabeledSeq = (SequenceLabel) gold;
        SequenceInstance sen = (SequenceInstance) input;

		int numOfLabels = lm.getNumOfLabels();
		int numOfTokens = sen.getConstituents().size();
		int numOfEmissionFeatures = lm.getNumOfFeature();
				
		float[][] dpTable = new float[2][numOfLabels];
		int[][] path = new int[numOfTokens][numOfLabels];

		int offset = (numOfEmissionFeatures + 1) * numOfLabels;

        // Temporary fix. This needs to be integrated with FeatureDefinitionBase somehow.
        int[] sentenceTokens = new int[numOfTokens];
        for (int i = 0; i < numOfTokens; i++) {
            Constituent c = sen.getConstituents().get(i);
            if (lm.containFeature("w:" + c.getSurfaceForm())) {
                sentenceTokens[i] = lm.getFeatureId("w:" + c.getSurfaceForm());
            } else {
                sentenceTokens[i] = lm.getFeatureId("w:unknownword");
            }
        }
    
		// Viterbi algorithm
		for (int j = 0; j < numOfLabels; j++) {
			float priorScore = wv.get(j);
			float zeroOrderScore =  wv.get(sentenceTokens[0] + j*numOfEmissionFeatures + numOfLabels) +
					((gold !=null && j != goldLabeledSeq.tagIds[0])?1:0);
			dpTable[0][j] = priorScore + zeroOrderScore; 	 
			path[0][j] = -1;
		}
		
		for (int i = 1; i < numOfTokens; i++) {
			for (int j = 0; j < numOfLabels; j++) {
				float zeroOrderScore = wv.get(sentenceTokens[i] + j*numOfEmissionFeatures + numOfLabels)
						+ ((gold!=null && j != goldLabeledSeq.tagIds[i])?1:0);
				
				float bestScore = Float.NEGATIVE_INFINITY;
				for (int k = 0; k < numOfLabels; k++) {
					float candidateScore = dpTable[(i-1)%2][k] +  wv.get(offset + (k * numOfLabels + j));
					if (candidateScore > bestScore) {
						bestScore = candidateScore;
						path[i][j] = k;
					}
				}
				dpTable[i%2][j] = zeroOrderScore + bestScore;
			}
		}

		// find the best sequence
		int[] labels = new int[numOfTokens];
		
		int maxTag = 0;
		for (int i = 0; i < numOfLabels; i++)
			if (dpTable[(numOfTokens - 1)%2][i] > dpTable[(numOfTokens - 1)%2][maxTag]) 
				maxTag = i;
		
		labels[numOfTokens - 1] = maxTag;
		
		for (int i = numOfTokens - 1; i >= 1; i--) 
			labels[i-1] = path[i][labels[i]];
		
		return new SequenceLabel(labels);
	}
	
	@Override
	public float getLoss(IInstance ins, IStructure goldStructure,  IStructure structure){
        SequenceLabel goldLabeledSeq = (SequenceLabel) goldStructure;
		float loss = 0;
		for (int i = 0; i < goldLabeledSeq.tagIds.length; i++)
			if (((SequenceLabel) structure).tagIds[i] != goldLabeledSeq.tagIds[i])
				loss += 1.0f;
		return loss;
	}

	@Override
	public IStructure getBestStructure(WeightVector wv,
			IInstance input) throws Exception {
		return getLossAugmentedBestStructure(wv, input, null);
	}

	@Override
	public Object clone(){
		return new ViterbiInferenceSolver(lm, featureGenerator);
	}
}
