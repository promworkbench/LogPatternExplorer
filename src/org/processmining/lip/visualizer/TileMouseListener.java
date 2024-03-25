package org.processmining.lip.visualizer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.deckfour.xes.model.XEvent;
import org.processmining.lip.controller.TController;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;

public class TileMouseListener implements MouseListener, MouseMotionListener {

	protected Tiles tiles;
	protected TController c;

	public TileMouseListener(Tiles tiles, TController controller) {
		this.tiles = tiles;
		if(controller == null){
			System.out.println("");
		}
		this.c = controller;
	}

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
			arg0.getComponent().revalidate();
			arg0.getComponent().repaint();
//			}
		}
	}

	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent arg0) {
		c.setOnTrace(tiles.getTrace());
	}

	public void mouseExited(MouseEvent arg0) {
		c.setOnTrace(null);

	}

	public void mouseMoved(MouseEvent arg0) {
		int coordX = arg0.getX();
		int coordY = arg0.getY();
		Tile t = tiles.retrieveTile(coordX, coordY);
		XEvent e = tiles.retrieveEvent(coordX, coordY);
		if (c != null) {
			if (e != null) {
				c.setOnEvent(e);
				c.setOnTile(t);
			} else {
				c.setOnEvent(null);
			}
		}
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseWheelMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
