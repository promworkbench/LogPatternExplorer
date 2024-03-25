package org.processmining.lip.detection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
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

public class PatternDetectionConcurrent implements IPatternDetection {

	public String toString() {
		return "Concurrent detector";
	}

	public List<ContextPattern<TNode>> detectPatterns(XLog log,
			PatternFactory<Tiles, Tile> factory, List<Tile> selectedTiles,
			Tiles selectedTileTrace, List<Tiles> allTiles) {
		List<ContextPattern<TNode>> patterns = new ArrayList<>();

		Tile center = selectedTiles.get(0);

		DAGraph<Tile> dagraph = selectedTileTrace.getDAGraph();
		List<Tile> concurTiles = dagraph.getConcurrentNodes(center);

		List<Tile> newList = new ArrayList<>();
		newList.add(center);
		newList.addAll(concurTiles);
		ContextPattern<TNode> p = factory.fitTilesToPattern(newList, selectedTileTrace);
		patterns.add(p);

		Multiset<String> concur = getMultiset(concurTiles);
		Set<Multiset<String>> concurSet = new THashSet<>();

		int i = 0;
		for (Tiles tiles : allTiles) {
			i++;
			if (i % 100 == 0) {
				System.out.println("Run " + i);
			}
			for (Tile t : tiles.getTiles()) {
				if (t.getLabel().equals(center.getLabel())) {
					List<Tile> newConcurTiles = tiles.getDAGraph().getConcurrentNodes(t);
					Multiset<String> newConcurLabels = getMultiset(newConcurTiles);
					if (!concurSet.contains(newConcurLabels)) {
						concurSet.add(newConcurLabels);
						newList = new ArrayList<>();
						newList.add(t);
						newList.addAll(newConcurTiles);
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

	private List<Tile> getTiles(Tiles selectedTileTrace, List<Integer> randomPattern) {
		List<Tile> newlist = new ArrayList<>();
		for (int i : randomPattern) {
			newlist.add(selectedTileTrace.getTiles()[i]);
		}
		return newlist;
	}

	private Set<List<Integer>> getRandomSet(int size, int centerID) {
		int num = 5;
		Set<List<Integer>> nums = new THashSet<>();
		Random r = new Random();

		for (int i = 0; i < num; i++) {
			Set<Integer> indices = new THashSet<>();
			indices.add(centerID);
			int patternSize = r.nextInt(5) + 2;
			for (int j = 0; j < patternSize; j++) {
				indices.add(r.nextInt(size));
			}

			nums.add(new ArrayList<>(indices));
		}
		return nums;
	}

	private List<Tile> getList(List<Tile> selectedTiles2, Tiles selectedTileTrace2) {
		List<Tile> list = new ArrayList<>(selectedTiles2);
		list.addAll(selectedTileTrace2.getDAGraph()
				.getDirectlyPredecessors(selectedTiles2.get(0)));
		list.addAll(selectedTileTrace2.getDAGraph()
				.getDirectlySuccessors(selectedTiles2.get(0)));

		return list;
	}

	public boolean requireUserInput() {
		return true;
	}

}
