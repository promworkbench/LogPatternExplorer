package org.processmining.lip.model.tilepo;

import java.awt.Dimension;

import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.visualizer.TileChartPOGraph;
import org.processmining.partialorder.models.graph.PartialOrderGraph;

public class TilesPO extends Tiles {

	private TileChartPOGraph panel;

	public TilesPO(Tile[] tiles, Dimension d, XTrace t) {
		super(tiles, d, t);
		// TODO Auto-generated constructor stub
	}

	public void setDrawPanel(PartialOrderGraph graph) {
		// TODO Auto-generated method stub
		this.panel = new TileChartPOGraph(graph);
	}
	
	public TileChartPOGraph getPanel(){
		return panel;
	}
	
	@Override
	public Dimension getDimension() {
		return new Dimension(500, 500);
	}
	
	@Override
	public int retrieveTileIndex(int coordX, int coordY) {
		TilePOEventNode node = panel.retrieveTileIndex( coordX, coordY);
		if(node == null){
			return -1;
		}
		return node.getID();
	}

}
