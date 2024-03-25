package org.processmining.lip.detection;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.model.pattern.ContextPattern;
import org.processmining.lip.model.pattern.PatternFactory;

public class PatternDetectionManualSelection implements IPatternDetection {


	public String toString(){
		return "Fit Pattern";
	}


	public List<ContextPattern<TNode>> detectPatterns(XLog log,
			PatternFactory<Tiles, Tile> factory, List<Tile> selectedTiles,
			Tiles selectedTileTrace, List<Tiles> allTiles) {
		ContextPattern<TNode> p = factory.fitTilesToPattern(selectedTiles,
				selectedTileTrace);
		List<ContextPattern<TNode>> list = new ArrayList<>();
		list.add(p);
		return list;
	}

	
	public boolean requireUserInput() {
		return true;
	}
}
