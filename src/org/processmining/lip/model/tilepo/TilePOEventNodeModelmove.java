package org.processmining.lip.model.tilepo;

import java.awt.Dimension;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.shapes.RoundedRect;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class TilePOEventNodeModelmove extends TilePOEventNode {

	public TilePOEventNodeModelmove(int trace, PartialOrderGraph parentGraph,
			int eventIndex, XEvent event) {
		super(trace, parentGraph, eventIndex, event);
		getAttributeMap().put(AttributeMap.SHAPE, new RoundedRect());
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(50, 30));
//		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);
		setColor(PGraphColorStyle.COLOR_MODEL_RELATION);
	}

}
