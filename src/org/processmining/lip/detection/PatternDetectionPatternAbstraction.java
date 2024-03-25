package org.processmining.lip.detection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.deckfour.xes.model.XLog;
import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.DAGraphImpl;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.TNodeImpl;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.model.pattern.ContextPattern;
import org.processmining.lip.model.pattern.ContextPatternGraphImpl;
import org.processmining.lip.model.pattern.PatternFactory;
import org.processmining.plugins.abstractions.enumtypes.PatternLengthPreference;
import org.processmining.plugins.abstractions.enumtypes.PatternType;
import org.processmining.plugins.abstractions.patterns.MinePatterns;

import gnu.trove.set.hash.THashSet;

public class PatternDetectionPatternAbstraction implements IPatternDetection {

	public String toString(){
		return "Pattern Abstraction (JC) detector";
	}
	
	public List<ContextPattern<TNode>> detectPatterns(XLog log,
			PatternFactory<Tiles, Tile> factory, List<Tile> selectedTiles,
			Tiles selectedTileTrace, List<Tiles> allTiles) {
		MinePatterns jcPatterns = new MinePatterns(null, log);
		Set<PatternType> patternTypeSet = new THashSet<>();
		patternTypeSet.add(PatternType.TandemArrays);
		patternTypeSet.add(PatternType.MaximalRepeats);

		jcPatterns.findPatterns(patternTypeSet, PatternLengthPreference.Shorter);
		List<List<String>> patterns = new ArrayList<>();
		for (Entry<TreeSet<String>, TreeSet<String>> entry : jcPatterns
				.getEntireLogMaximalRepeatBasePatternAlphabetPatternSetMap().entrySet()) {

			System.out.println("Pattern: ");
//			for (String s : entry.getKey()) {
//				System.out.println(s + " : "
//						+ jcPatterns.getEncodedLog().getCharActivityMap().get(s));
//			}
//			System.out.println("Pattern Alphabet: " + entry.getKey());
//			System.out.println("Decoded Pattern Alphabet: " + decoded);
//			System.out.println("values: ");

			for (String s : entry.getValue()) {
				System.out.println(s);
				List<String> seqPattern = new ArrayList<>();

				for (int i = 0; i < s.length(); i++) {
					String string = jcPatterns.getEncodedLog().getCharActivityMap()
							.get(s.substring(i, i + 1));
					System.out.println(string);
					String label = string.indexOf("-") == -1 ? string
							: string.substring(0, string.indexOf("-"));
					seqPattern.add(label);
				}
				if (seqPattern.size() > 1) {
					patterns.add(seqPattern);
				}
			}
		}
		return convertJSPatternToPattern(patterns);

	}

	private List<ContextPattern<TNode>> convertJSPatternToPattern(
			List<List<String>> seqPatterns) {
		List<ContextPattern<TNode>> patterns = new ArrayList<>();

		for (List<String> seqPattern : seqPatterns) {
			int i = 0;
			List<TNode> nodes = new ArrayList<TNode>();
			for (String ev : seqPattern) {
				TNode n = new TNodeImpl(i++, ev);
				nodes.add(n);
			}

			TNode center = nodes.get(0);
			boolean[][] directlyCauses = new boolean[nodes.size()][nodes.size()];
			boolean[][] eventuallyCauses = new boolean[nodes.size()][nodes.size()];
			for (int j = 1; j < seqPattern.size(); j++) {
				directlyCauses[j - 1][j] = true;
			}

			DAGraph<TNode> graph = new DAGraphImpl<>(nodes, directlyCauses,
					eventuallyCauses);
			ContextPattern<TNode> pattern = new ContextPatternGraphImpl(center, graph);
			patterns.add(pattern);
		}
		return patterns;
	}
	
	public boolean requireUserInput() {
		return false;
	}

}
