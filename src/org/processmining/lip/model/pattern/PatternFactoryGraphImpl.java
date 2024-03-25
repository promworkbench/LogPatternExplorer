package org.processmining.lip.model.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public class PatternFactoryGraphImpl implements PatternFactory<Tiles, Tile> {

	// Assumption: the first tile is the center event of the context.
	public ContextPattern<TNode> fitTilesToPattern(List<Tile> tiles, Tiles tileTrace) {

		Tile center = tiles.get(0);

		DAGraph<Tile> dagraph = tileTrace.getDAGraph();

		DAGraph<Tile> projected = dagraph.projectOn(tiles);

		ContextPattern<TNode> pattern = new ContextPatternGraphImpl(center, projected);
		return pattern;
	}

	public PatternInstances<Tile> matchEventToPattern(List<Tiles> allTiles,
			ContextPattern<TNode> p) {
		List<PatternInstance<Tile>> instances = new ArrayList<>();
		List<PatternInstance<Tile>> antiInstances = new ArrayList<>();

		for (Tiles tileTrace : allTiles) {

			Map<String, Set<Tile>> candidates = new THashMap<String, Set<Tile>>();

			// 1. For each label in pattern, get its candidates
			for (TNode patternTile : p.getGraph().getNodes()) {
				candidates.put(patternTile.getLabel(), new THashSet<Tile>());
			}
			for (Tile instanceTile : tileTrace.getTiles()) {
				Set<Tile> ts = candidates.get(instanceTile.getLabel());
				if (ts != null) {
					ts.add(instanceTile);
				}
			}

			// 2. Try for a candidate of the center tile
			for (Tile instanceCenterCdt : candidates.get(p.getLabel())) {

				Map<TNode, Tile> isoMapCandidateSoFar = new THashMap<>();
				isoMapCandidateSoFar.put(p.getCenter(), instanceCenterCdt);

				List<TNode> contextTiles = new ArrayList<>(p.getGraph().getNodes());
				contextTiles.remove(p.getCenter());

				boolean valid = recursePruning(p, tileTrace, contextTiles, candidates,
						isoMapCandidateSoFar);
				if (valid) {
					PatternInstance<Tile> instance = new PatternInstance<Tile>(tileTrace,
							instanceCenterCdt,
							new ArrayList<>(isoMapCandidateSoFar.values()));
//					for (Tile t : isoMapCandidateSoFar.values()) {
//						t.setColor(Color.RED);
//					}
					instances.add(instance);
				} else {
					PatternInstance<Tile> instance = new PatternInstance<Tile>(tileTrace,
							instanceCenterCdt, new ArrayList<Tile>());
					antiInstances.add(instance);
				}
			}
		}

		PatternInstances<Tile> instanceCollection = new PatternInstances<>();
		instanceCollection.setInstances(instances);
		instanceCollection.setAntiInstances(antiInstances);

		return instanceCollection;
	}

	protected boolean recursePruning(ContextPattern<TNode> pattern, Tiles instanceTiles,
			List<TNode> patternTiles, Map<String, Set<Tile>> candidatesTiles,
			Map<TNode, Tile> candidateSoFar) {

		// 1. base case
		if (patternTiles.isEmpty()) {
			return true;
		}

		// 2.1 recurse : select the source node to be mapped
		TNode patternTile = patternTiles.remove(0);
		Set<Tile> aCandidateTileSet = new THashSet<>(
				candidatesTiles.get(patternTile.getLabel()));
		
		aCandidateTileSet.removeAll(candidateSoFar.values());

		// 2.2: select the target node to be mapped. 
		for (Tile candidateTile : aCandidateTileSet) {

			if (!candidateSoFar.values().contains(candidateTile) && isValid(pattern,
					instanceTiles, candidateSoFar, patternTile, candidateTile)) {

				// 2.3 add the choice to continue with recursion
				candidateSoFar.put(patternTile, candidateTile);

				boolean valid = recursePruning(pattern, instanceTiles, patternTiles,
						candidatesTiles, candidateSoFar);

				if (valid) {
					return true;
				}
				candidateSoFar.remove(patternTile);// undo choice. 

			}

		}
		patternTiles.add(0, patternTile); // undo choice. 

		return false;
	}

	private boolean isValid(ContextPattern<TNode> pattern, Tiles instanceTiles,
			Map<TNode, Tile> candidateSoFar, TNode patternTile, Tile candidateTile) {

		DAGraph<TNode> patternGraph = pattern.getGraph();
		DAGraph<Tile> candidateGraph = instanceTiles.getDAGraph();

		for (Entry<TNode, Tile> entry : candidateSoFar.entrySet()) {

			boolean valid = !patternGraph.isDirectlyCauses(entry.getKey(), patternTile)
					|| candidateGraph.isDirectlyCauses(entry.getValue(), candidateTile);
			valid &= !patternGraph.isDirectlyCauses(patternTile, entry.getKey())
					|| candidateGraph.isDirectlyCauses(candidateTile, entry.getValue());
			
			valid &= patternGraph.isEventuallyCauses(patternTile,
					entry.getKey()) == candidateGraph.isEventuallyCauses(candidateTile,
							entry.getValue());
			valid &= patternGraph.isEventuallyCauses(entry.getKey(),
					patternTile) == candidateGraph.isEventuallyCauses(entry.getValue(),
							candidateTile);

			if (!valid) {
				return false;
			}

		}
		return true;
	}

//	public ContextPattern fitEventsToPattern(List<XEvent> events) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	public List<PatternInstance> matchEventToPattern(XLog log, ContextPattern pattern) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//

}
