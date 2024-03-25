package org.processmining.lip.detection.dev;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.processmining.lip.detection.IPatternDetection;
import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.model.pattern.ContextPattern;
import org.processmining.lip.model.pattern.PatternFactory;
import org.processmining.partialorder.models.graph.node.POEventNode;
import org.processmining.xesalignmentextension.XAlignmentExtension;
import org.processmining.xesalignmentextension.XAlignmentExtension.MoveType;
import org.processmining.xesalignmentextension.XAlignmentExtension.XAlignmentMove;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import gnu.trove.set.hash.THashSet;

public class PatternDetectionReplacement implements IPatternDetection {

	public String toString() {
		return "POA-Replacement detector";
	}

	public List<ContextPattern<TNode>> detectPatterns(XLog log,
			PatternFactory<Tiles, Tile> factory, List<Tile> selectedTiles,
			Tiles selectedTileTrace, List<Tiles> allTiles) {
		List<ContextPattern<TNode>> patterns = new ArrayList<>();

		Set<Multiset<String>> replacements = new THashSet<>();

		//Tile center = selectedTiles.get(0);
		for (Tiles ts : allTiles) {
			for (Tile t : ts) {
				if (!isVisibleModelMove(t)) {
					continue; //skip
				}
				// t is a visible model move
				// t is core-node

				DAGraph<Tile> dagraph = ts.getDAGraph();
				List<Tile> concurTiles = dagraph.getConcurrentNodes(t);

//				for (Tile concurTile : concurTiles) {
				if(concurTiles.size() == 1) {
					Tile concurTile = concurTiles.get(0);
					if (!isLogMove(concurTile)) {
						continue;
					}
					List<Tile> newList = new ArrayList<>();
					newList.add(t);
					newList.add(concurTile);

					// Simple format of replacement patterns
					Multiset<String> concur = getMultiset(newList);

					// Check if replacement pattern already detected
					if (replacements.contains(concur)) {
						// already detected, then skip
					} else {
						ContextPattern<TNode> p = factory.fitTilesToPattern(newList, ts);
						patterns.add(p);
						replacements.add(concur);
					}
				}
			}
		}
		return patterns;
	}

	private boolean isLogMove(Tile t) {
		if (t instanceof POEventNode) {
			XEvent e = ((POEventNode) t).getEvent();
			XAlignmentMove move = XAlignmentExtension.instance().extendEvent(e);
			if (move.getType().equals(MoveType.LOG)) {
				//				if(move.isObservable()){
				return true;
				//				}
			}
		}
		return false;
	}

	private boolean isVisibleModelMove(Tile t) {
		if (t instanceof POEventNode) {
			XEvent e = ((POEventNode) t).getEvent();
			XAlignmentMove move = XAlignmentExtension.instance().extendEvent(e);
			if (move.getType().equals(MoveType.MODEL)) {
				if (move.isObservable()) {
					return true;
				}
			}
		}
		return false;
	}

	// TODO: duplicated code.
	private Multiset<String> getMultiset(Collection<Tile> concurTiles) {
		Multiset<String> set = HashMultiset.create();
		for (Tile t : concurTiles) {
			set.add(t.getLabel());
		}
		return set;
	}

	public boolean requireUserInput() {
		return false;
	}

}
