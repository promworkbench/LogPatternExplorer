package org.processmining.lip.detection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.model.pattern.ContextPattern;
import org.processmining.lip.model.pattern.PatternFactory;

import gnu.trove.set.hash.THashSet;

public class PatternDetectionRandom implements IPatternDetection {

	public String toString(){
		return "Random detector";
	}
	
	public List<ContextPattern<TNode>> detectPatterns(XLog log,
			PatternFactory<Tiles, Tile> factory, List<Tile> selectedTiles,
			Tiles selectedTileTrace, List<Tiles> allTiles) {

		List<ContextPattern<TNode>> patterns = new ArrayList<>();
		ContextPattern<TNode> p = factory.fitTilesToPattern(selectedTiles,
				selectedTileTrace);
		patterns.add(p);

		List<Tile> selectedTiles2 = getList(selectedTiles, selectedTileTrace);
		ContextPattern<TNode> p2 = factory.fitTilesToPattern(selectedTiles2,
				selectedTileTrace);
		patterns.add(p2);

		Set<List<Integer>> set = getRandomSet(selectedTiles.size(),
				selectedTiles.get(0).getID());
		for (List<Integer> randomPattern : set) {
			List<Tile> tiles = getTiles(selectedTileTrace, randomPattern);
			ContextPattern<TNode> p3 = factory.fitTilesToPattern(tiles,
					selectedTileTrace);
			patterns.add(p3);
		}

		return patterns;
	}

	// Start Testing code --------------------------------------------------------
	// TODO: trying out pattern generations...

//	public Set<ContextPattern<TNode>> generateTilesToPattern(List<Tile> selectedTiles,
//			Tiles selectedTileTrace, List<Tiles> list) {
//
//	}

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
