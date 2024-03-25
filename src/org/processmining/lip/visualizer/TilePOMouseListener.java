package org.processmining.lip.visualizer;

import java.awt.Color;
import java.awt.event.MouseEvent;

import org.processmining.lip.controller.TController;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;
import org.processmining.models.jgraph.ProMJGraph;

public class TilePOMouseListener extends TileMouseListener {

	private ProMJGraph comp;

	public TilePOMouseListener(Tiles tiles, TController controller,
			ProMJGraph component) {
		super(tiles, controller);
		this.comp = component;

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		int coordX = arg0.getX();
		int coordY = arg0.getY();
//		for(int i )
		Tile e = tiles.retrieveTile(coordX, coordY);
		if (c != null && e != null) {
//			if () {
			e.setSelected(!e.isSelected());
			c.setSelectedTileTrace(tiles);
			c.setSelectedTile(e, e.isSelected());
//			comp.getModel().beginUpdate();
			if (e.isSelected()) {

				e.setLineColor(Color.RED);
			} else {
				e.setLineColor(Color.BLACK);
			}
//			comp.getModel().endUpdate();
			comp.refresh();
//			comp.revalidate();
//			comp.repaint();

			arg0.getComponent().revalidate();
			arg0.getComponent().repaint();

		}

	}
}
