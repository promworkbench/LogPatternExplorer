package org.processmining.lip.controller;

import org.deckfour.xes.model.XEvent;
import org.processmining.lip.model.tilepo.TilePOEventNode;
import org.processmining.lip.model.tilepo.TilePOEventNodeLogmove;
import org.processmining.lip.model.tilepo.TilePOEventNodeModelSilentmove;
import org.processmining.lip.model.tilepo.TilePOEventNodeModelmove;
import org.processmining.lip.model.tilepo.TilePOEventNodeSyncMove;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.xesalignmentextension.XAlignmentExtension;
import org.processmining.xesalignmentextension.XAlignmentExtension.MoveType;
import org.processmining.xesalignmentextension.XAlignmentExtension.XAlignmentMove;

public class TileLayoutPOAs extends TileLayoutPOTs {

	protected static TileLayoutPOTs poainstance = null;


	protected TileLayoutPOAs() {
		// Exists only to defeat instantiation.
	}

	public static TileLayoutPOTs getInstance() {
		if (poainstance == null) {
			poainstance = new TileLayoutPOAs();
		}
		return poainstance;
	}

	
//	XAlignmentExtension ext = 
	public String toString() {
		return "PO-Alignment Extension Tiles";
	}

	protected TilePOEventNode getTilePONode(int traceIndex, PartialOrderGraph graph,
			int i, XEvent event) {
		
		XAlignmentMove move = XAlignmentExtension.instance().extendEvent(event);
		if(move.getType().equals(MoveType.LOG)){
			return new TilePOEventNodeLogmove(traceIndex, graph, i, event);
		} else if(move.getType().equals(MoveType.MODEL)){
			if(move.isObservable()){
				return new TilePOEventNodeModelmove(traceIndex, graph, i, event);
			} else {
				return new TilePOEventNodeModelSilentmove(traceIndex, graph, i, event);
			}
		} else {
			return new TilePOEventNodeSyncMove(traceIndex, graph, i, event);
		} 
//		return null;
	}

}
