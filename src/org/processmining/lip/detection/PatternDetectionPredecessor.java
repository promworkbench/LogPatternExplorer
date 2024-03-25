package org.processmining.lip.detection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.model.pattern.ContextPattern;
import org.processmining.lip.model.pattern.PatternFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import gnu.trove.set.hash.THashSet;
/**
 * 
 * @author xlu
 */
public class PatternDetectionPredecessor implements IPatternDetection {

	public String toString() {
		return "Direct-predecessor detector";
	}

	public List<ContextPattern<TNode>> detectPatterns(XLog log,
			PatternFactory<Tiles, Tile> factory, List<Tile> selectedTiles,
			Tiles selectedTileTrace, List<Tiles> allTiles) {
		List<ContextPattern<TNode>> patterns = new ArrayList<>();

		Tile center = selectedTiles.get(0);

		DAGraph<Tile> dagraph = selectedTileTrace.getDAGraph();
		List<Tile> predTiles = dagraph.getDirectlyPredecessors(center);

		List<Tile> newList = new ArrayList<>();
		newList.add(center);
		newList.addAll(predTiles);
		ContextPattern<TNode> p = factory.fitTilesToPattern(newList, selectedTileTrace);
		patterns.add(p);

		Multiset<String> pred = getMultiset(predTiles);
		Set<Multiset<String>> predSet = new THashSet<>();
		predSet.add(pred);

		for (Tiles tiles : allTiles) {

			for (Tile t : tiles.getTiles()) {
				if (t.getLabel().equals(center.getLabel())) {

					List<Tile> newPredTiles = tiles.getDAGraph()
							.getDirectlyPredecessors(t);
					Multiset<String> newPredLabels = getMultiset(newPredTiles);
					if (!predSet.contains(newPredLabels)) {
						predSet.add(newPredLabels);
						newList = new ArrayList<>();
						newList.add(t);
						newList.addAll(newPredTiles);
						p = factory.fitTilesToPattern(newList, tiles);
						patterns.add(p);

					}

				}
			}
		}

		return patterns;
	}

	private Multiset<String> getMultiset(Collection<Tile> concurTiles) {
		Multiset<String> set = HashMultiset.create();
		for (Tile t : concurTiles) {
			set.add(t.getLabel());
		}

		return set;
	}

	public boolean requireUserInput() {
		return true;
	}

}
