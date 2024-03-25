package org.processmining.lip.model.tilepo;

import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.shape.ShapeSyncMove;
import org.processmining.partialorder.plugins.vis.PGraphColorStyle;

public class TilePOEventNodeSyncMove extends TilePOEventNode {

	public TilePOEventNodeSyncMove(int trace, PartialOrderGraph parentGraph,
			int eventIndex, XEvent event) {
		super(trace, parentGraph, eventIndex, event);
		getAttributeMap().put(AttributeMap.SHAPE, new ShapeSyncMove());
//		getAttributeMap().put(AttributeMap.SHAPE, new RoundedRect());
//		getAttributeMap().put(AttributeMap.SIZE, new Dimension(60, 30));
//		getAttributeMap().put(AttributeMap.BORDERWIDTH, 2);

//		getAttributeMap().put(AttributeMap.SHOWLABEL, true);
		setColor(PGraphColorStyle.COLOR_SYNC_RELATION);
	}

}
