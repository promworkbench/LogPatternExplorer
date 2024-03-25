package org.processmining.lip.model.tilepo;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.shape.ShapeLogMove;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class TilePOEventNodeLogmove extends TilePOEventNode {

	public TilePOEventNodeLogmove(int trace, PartialOrderGraph parentGraph,
			int eventIndex, XEvent event) {
		super(trace, parentGraph, eventIndex, event);
		getAttributeMap().put(AttributeMap.SHAPE, new ShapeLogMove());
		setColor(PGraphColorStyle.COLOR_LOG_RELATION);
	}

}
