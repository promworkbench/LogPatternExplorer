package org.processmining.lip.model.tilepo;

import java.awt.Color;
import java.awt.Graphics;

import org.deckfour.xes.model.XEvent;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.pattern.vis.ColorUtil;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.node.POEventNode;

public class TilePOEventNode extends POEventNode implements Tile {

	int id;
	private boolean isSeleted;
	private boolean isCenter;
	private boolean isAnti;
	private int intLetter;
	private Color lineColor;

	private ProMJGraph parent;

	public TilePOEventNode(int trace, PartialOrderGraph parentGraph, int eventIndex,
			XEvent event) {
		super(trace, parentGraph, eventIndex, event);
	}

	public int getID() {
		return id;
	}

	public void setID(int i) {
		id = i;
	}

	public void setSelected(boolean b) {
		this.isSeleted = b;
	}

	public void setLineColor(Color c) {
		this.lineColor = c;
		this.getAttributeMap().put(AttributeMap.STROKECOLOR, c);
		this.getAttributeMap().put(AttributeMap.BORDERWIDTH, 4);
		refresh();
	}

	@Override
	public void setColor(Color color) {
		super.setColor(color);
		this.getAttributeMap().put(AttributeMap.FILLCOLOR, color);
		this.getAttributeMap().put(AttributeMap.LABELCOLOR,
				ColorUtil.getContrastColor(color));
		refresh();
	}

	private void refresh() {
		if (getParent() != null) {
			getParent().refresh();
		}
	}

	public void setCenter(boolean b) {
		this.isCenter = b;
	}

	public void setAnti(boolean b) {
		this.isAnti = b;
	}

	public void setLabelLetters(int tileLabelLetter) {
		this.intLetter = tileLabelLetter;
	}

	public boolean isSelected() {
		return isSeleted;
	}

	public void paintComponent(Graphics g) {
		// TODO Auto-generated method stub

	}

	public ProMJGraph getParent() {
		return parent;
	}

	public void setParent(ProMJGraph parent) {
		this.parent = parent;
	}

}
