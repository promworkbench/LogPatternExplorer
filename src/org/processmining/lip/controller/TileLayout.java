package org.processmining.lip.controller;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.TileSize;
import org.processmining.lip.model.Tiles;

public interface TileLayout {

	public Tiles computeTiles(XTrace trace, int x, int y, int width, int height,
			int tileSpace);

	public Tiles computeTiles(XTrace trace, TileSize tileSize);
	
	public void setClassifier(XEventClassifier classifier);

}
