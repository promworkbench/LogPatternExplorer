package org.processmining.lip.controller;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.DAGraphImpl;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.TileImpl;
import org.processmining.lip.model.Tiles;

public class TileLayoutStack extends TileLayoutAbstract implements TileLayout {

	private static TileLayoutStack instance = null;

	private static int sec = 1;

	protected TileLayoutStack() {
		// Exists only to defeat instantiation.
	}

	public static TileLayoutStack getInstance() {
		if (instance == null) {
			instance = new TileLayoutStack();
		}
		return instance;
	}

	public String toString() {
		return "Same time";
	}

	public Tiles computeTiles(XTrace trace, int x, int y, int width, int height,
			int tileSpace) {

		int i = x;
		int j = y;

		int maxX = 0;
		int maxY = 0;

		Tile[] tiles = new Tile[trace.size()];

		boolean[][] dfg = new boolean[trace.size()][trace.size()];

		Date prevDate = null;
		Set<Integer> prevSet = new HashSet<Integer>();
		Set<Integer> currSet = new HashSet<Integer>();

		for (int k = 0; k < trace.size(); k++) {
			XEvent e = trace.get(k);
			Date currentDate = XTimeExtension.instance().extractTimestamp(e);
			if (prevDate != null) {
				long diffInSec = 0;
				if (currentDate != null) {
					diffInSec = (currentDate.getTime() - prevDate.getTime()) / 1000;
				}

//				if (diffInSec > sec) {
//					// increase coordinate in x-axis
//					i += width + tileSpace;
//					j = y;
//					maxX = i > maxX ? i : maxX;
//					
//					prevSet = currSet;
//					currSet = new HashSet<Integer>();
//				} else {
//					// increase coordinate in y-axis
//					j += height + tileSpace;
//					maxY = j > maxY ? j : maxY;
//				}

				if (diffInSec < sec) {
					// increase coordinate in y-axis
					j += height + tileSpace;
					maxY = j > maxY ? j : maxY;

				} else {
					// increase coordinate in x-axis
					i += width + tileSpace;
					j = y;
					maxX = i > maxX ? i : maxX;

					prevSet = currSet;
					currSet = new HashSet<Integer>();
				}

			}

			currSet.add(k);
			for (Integer pred : prevSet) {
				dfg[pred][k] = true;
			}

			tiles[k] = new TileImpl(i, j, width, height);
			tiles[k].setLabel(this.getClassifier().getClassIdentity(trace.get(k)));
			tiles[k].setID(k);

			prevDate = currentDate;
		}

		Tiles t = new Tiles(tiles, new Dimension(maxX, maxY + height + tileSpace), trace);

		DAGraph<Tile> dag = new DAGraphImpl<>(Arrays.asList(tiles), dfg);
		t.setDAGraph(dag);

		return t;
	}

	public void setTimeDur(int threshold) {
		this.sec = threshold;
	}

	public int getTimeDur() {
		return this.sec;
	}

}
