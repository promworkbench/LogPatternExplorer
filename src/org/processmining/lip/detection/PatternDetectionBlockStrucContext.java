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

public class PatternDetectionBlockStrucContext implements IPatternDetection {

	public class BlockStrucContext {
		public String center;
		public Multiset<String> preds;
		public Multiset<String> concurs;
		public Multiset<String> succs;

	}

	public String toString() {
		return "Direct context detector";
	}

	public List<ContextPattern<TNode>> detectPatterns(XLog log,
			PatternFactory<Tiles, Tile> factory, List<Tile> selectedTiles,
			Tiles selectedTileTrace, List<Tiles> allTiles) {
		List<ContextPattern<TNode>> patterns = new ArrayList<>();

		Tile center = selectedTiles.get(0);

		BlockStrucContext context = extractBlockStrucContext(selectedTileTrace, center);
		List<Tile> newList = getContextTiles(selectedTileTrace, center);
		ContextPattern<TNode> p = factory.fitTilesToPattern(newList, selectedTileTrace);
		patterns.add(p);

		Set<BlockStrucContext> contextSet = new THashSet<>();
		contextSet.add(context);

		for (Tiles tiles : allTiles) {

			for (Tile t : tiles.getTiles()) {
				if (t.getLabel().equals(center.getLabel())) {

					if (getMultiset(tiles.getDAGraph().getDirectlyPredecessors(t))
							.contains("W_Valideren aanvraag")) {
						System.out.println("");
					} else {
						System.out.println("");
					}
					BlockStrucContext newContext = extractBlockStrucContext(tiles, t);

					if (!doesSetContain(contextSet, newContext)) {
						contextSet.add(newContext);

						newList = getContextTiles(tiles, t);
						p = factory.fitTilesToPattern(newList, tiles);
						patterns.add(p);
//
					}
//
				}
			}
		}

		return patterns;
	}

	private List<Tile> getContextTiles(Tiles tiles, Tile tCenter) {
		List<Tile> newList = new ArrayList<>();
		newList.add(tCenter);
		newList.addAll(tiles.getDAGraph().getDirectlyPredecessors(tCenter));
		newList.addAll(tiles.getDAGraph().getConcurrentNodes(tCenter));
		newList.addAll(tiles.getDAGraph().getDirectlySuccessors(tCenter));
		return newList;
	}

	private boolean doesSetContain(Set<BlockStrucContext> contextSet,
			BlockStrucContext newContext) {

		for (BlockStrucContext context : contextSet) {
			if (context.concurs.equals(newContext.concurs)
					&& context.preds.equals(newContext.preds)
					&& context.succs.equals(newContext.succs)) {
				return true;
			}

		}
		return false;
	}

	private BlockStrucContext extractBlockStrucContext(Tiles selectedTileTrace,
			Tile center) {
		DAGraph<Tile> dagraph = selectedTileTrace.getDAGraph();
		List<Tile> concurTiles = dagraph.getConcurrentNodes(center);
		List<Tile> predTiles = dagraph.getDirectlyPredecessors(center);
		List<Tile> succTiles = dagraph.getDirectlySuccessors(center);

		BlockStrucContext context = new BlockStrucContext();
		context.concurs = getMultiset(concurTiles);
		context.preds = getMultiset(predTiles);
		context.succs = getMultiset(succTiles);
		return context;
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
