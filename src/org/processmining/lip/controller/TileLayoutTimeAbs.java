package org.processmining.lip.controller;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.DAGraphImpl;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.TileImpl;
import org.processmining.lip.model.Tiles;

public class TileLayoutTimeAbs extends TileLayoutAbstract implements TileLayout {
	private static TileLayoutTimeAbs instance = null;

	private int window_Width = 500;
	private Date endDate;
	private Date startDate;

	public TileLayoutTimeAbs(Date start, Date end) {
		this.startDate = start;
		this.endDate = end;
	}

	public TileLayoutTimeAbs(Date start, Date end, int windowWidth) {
		this(start, end);
		this.window_Width = windowWidth;
	}

	public String toString() {
		return "Scaled on timestamps";
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public int getWindowWidth() {
		return window_Width;
	}

	public void setWindowWidth(int window_Width) {
		this.window_Width = window_Width;
	}

	public void setTimeBounds(Date start, Date end) {
		this.startDate = start;
		this.endDate = end;
	}

	public Tiles computeTiles(XTrace trace, int x, int y, int width, int height,
			int tileSpace) {

		Tile[] tiles = new Tile[trace.size()];

		int i = Integer.MIN_VALUE;
		int j = y + tileSpace;

		int maxX = 0;
		int maxY = 0;

//		Date prevDate = null;
		
		Set<Integer> prevSet = new HashSet<Integer>();
		Set<Integer> currSet = new HashSet<Integer>();
		boolean[][] dfg = new boolean[trace.size()][trace.size()];
		
		for (int k = 0; k < trace.size(); k++) {
			XEvent e = trace.get(k);
			Date currentDate = XTimeExtension.instance().extractTimestamp(e);
			String label = XConceptExtension.instance().extractName(e);
			long diffInMillies = currentDate.getTime() - getStartDate().getTime();
			long diffWindow = getEndDate().getTime() - getStartDate().getTime();
			int newX = (int) (diffInMillies * window_Width / diffWindow) + tileSpace;

			if (newX < i + width + tileSpace) {
				j += height + tileSpace;
				maxY = j > maxY ? j : maxY;
			} else {
				// decrease coordinate in y-axis to 0. 
				j = 0;
				
				prevSet = currSet;
				currSet = new HashSet<Integer>();
			}
			
			currSet.add(k);
			for(Integer pred : prevSet){
				dfg[pred][k] = true;
			}

			tiles[k] = new TileImpl(newX, j, width, height);
			tiles[k].setLabel(this.getClassifier().getClassIdentity(trace.get(k)));
			tiles[k].setID(k);
			
			i = newX;
		}

		Tiles t = new Tiles(tiles, new Dimension(window_Width, maxY  + height + tileSpace), trace);
		DAGraph<Tile> dag = new DAGraphImpl<>(Arrays.asList(tiles), dfg);
		t.setDAGraph(dag);
		return t;
	}

}
