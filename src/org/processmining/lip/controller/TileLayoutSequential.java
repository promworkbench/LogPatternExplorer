package org.processmining.lip.controller;

import java.awt.Dimension;
import java.util.Arrays;

import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.DAGraphImpl;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.TileImpl;
import org.processmining.lip.model.Tiles;

public class TileLayoutSequential extends TileLayoutAbstract implements TileLayout {

	private static TileLayoutSequential instance = null;

	protected TileLayoutSequential() {
		// Exists only to defeat instantiation.
	}

	public static TileLayoutSequential getInstance() {
		if (instance == null) {
			instance = new TileLayoutSequential();
		}
		return instance;
	}

	public Tiles computeTiles(XTrace trace, int x, int y, int width, int height, int tileSpace) {

		Tile[] tiles = new Tile[trace.size()];

		boolean[][] dfg = new boolean[trace.size()][trace.size()];
		for (int i = 0; i < trace.size(); i++) {
			x += width + tileSpace;
			tiles[i] = new TileImpl(x, y, width, height);
//			tiles[i].setLabel(XConceptExtension.instance().extractName(trace.get(i)));
			tiles[i].setLabel(this.getClassifier().getClassIdentity(trace.get(i)));
			tiles[i].setID(i);
			
			if (i > 0) {
				dfg[i - 1][i] = true;
			}
		}

		Dimension d = new Dimension(x + width + tileSpace, tileSpace + height + tileSpace);

		Tiles t = new Tiles(tiles, d, trace);
		DAGraph<Tile> dag = new DAGraphImpl<>(Arrays.asList(tiles), dfg);
		t.setDAGraph(dag);
		return t;
	}

	public String toString() {
		return "Sequential Tiles";
	}

}
