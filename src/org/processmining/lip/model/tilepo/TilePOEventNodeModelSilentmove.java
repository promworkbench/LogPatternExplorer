package org.processmining.lip.model.tilepo;

import java.awt.Dimension;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class TilePOEventNodeModelSilentmove extends TilePOEventNodeModelmove {

	public TilePOEventNodeModelSilentmove(int trace, PartialOrderGraph parentGraph,
			int eventIndex, XEvent event) {
		super(trace, parentGraph, eventIndex, event);
		
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(30, 30));
//		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);
		setColor(PGraphColorStyle.NODE_INVISIBLE_MOVE_COLOR);
	}

}
