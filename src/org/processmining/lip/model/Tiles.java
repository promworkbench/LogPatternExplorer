package org.processmining.lip.model;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Iterator;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

public class Tiles implements Iterable<Tile> {

	private XTrace trace;
	private Tile[] tiles;

	private Dimension dimension;

	private DAGraph<Tile> dfg;
	private int traceIndex;

	public Tiles(Tile[] tiles, Dimension d, XTrace t) {
		this.tiles = tiles;
		this.dimension = new Dimension(d.width, d.height + 5);
		this.trace = t;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public Tile[] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[] tiles) {
		this.tiles = tiles;
	}

	public XEvent retrieveEvent(int coordX, int coordY) {
		int index = retrieveTileIndex(coordX, coordY);
		if (index >= 0) {
			return this.trace.get(index);
		}
		return null;
	}

	public Tile retrieveTile(int coordX, int coordY) {
		int index = retrieveTileIndex(coordX, coordY);
		if (index >= 0) {
			return tiles[index];
		}
		return null;
	}

	public int retrieveTileIndex(int coordX, int coordY) {
		for (int i = 0; i < tiles.length; i++) {
			if (((TileImpl)tiles[i]).isInTile(coordX, coordY)) {
				return i;
			}
		}
		return -1;
	}

	public Iterator<Tile> iterator() {
		return Arrays.asList(tiles).iterator();
	}

	public XTrace getTrace() {
		return trace;
	}

	public DAGraph<Tile> getDAGraph() {
		return dfg;
	}

	public void setDAGraph(DAGraph<Tile> dfg) {
		this.dfg = dfg;
	}

	public void setTraceIndex(int trcIndex) {
		this.traceIndex = trcIndex;
	}

	public int getTraceIndex() {
		return traceIndex;
	}
}
