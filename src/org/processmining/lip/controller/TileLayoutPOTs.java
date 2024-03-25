package org.processmining.lip.controller;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeList;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.DAGraphImpl;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.model.tilepo.TilePOEventNode;
import org.processmining.lip.model.tilepo.TilesPO;
import org.processmining.partialorder.models.graph.POEdge;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.models.graph.edge.POEdgeImp;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class TileLayoutPOTs extends TileLayoutAbstract implements TileLayout {
	
	

	protected static TileLayoutPOTs poinstance = null;
	
	public String toString() {
		return "PO-Traces Extension Tiles";
	}

	protected TileLayoutPOTs() {
		// Exists only to defeat instantiation.
	}

	public static TileLayoutPOTs getInstance() {
		if (poinstance == null) {
			poinstance = new TileLayoutPOTs();
		}
		return poinstance;
	}

	public Tiles computeTiles(XTrace trace, int x, int y, int width, int height,
			int tileSpace) {

		Tile[] tiles = new Tile[trace.size()];
		boolean[][] dfg = new boolean[trace.size()][trace.size()];

//		for (int i = 0; i < trace.size(); i++) {
//			x += width + tileSpace;
//			tiles[i] = new TileImpl(x, y, width, height);
////			tiles[i].setLabel(XConceptExtension.instance().extractName(trace.get(i)));
//			tiles[i].setLabel(this.getClassifier().getClassIdentity(trace.get(i)));
//			tiles[i].setID(i);
//			
//			if (i > 0) {
//				dfg[i - 1][i] = true;
//			}
//		}

//		XTrace trace = ptrace.getTrace();
		int traceIndex = -1;
		XConceptExtension ce = XConceptExtension.instance();
		PartialOrderGraph graph = new PartialOrderGraph(ce.extractName(trace),
				traceIndex);
		TIntObjectMap<PONode> map = new TIntObjectHashMap<PONode>();

		Map<XID, Integer> mapId2Index = new THashMap<>();

		// add nodes
		for (int i = 0; i < trace.size(); i++) {
			XEvent event = trace.get(i);
			PONode node = getTilePONode(traceIndex, graph, i, event);
			node.setLabel(getClassifier().getClassIdentity(event));

			graph.addNode(node);
			map.put(i, node);

			XAttribute xid = event.getAttributes().get("po:id");
			if (xid != null && xid instanceof XAttributeID) {
				mapId2Index.put(((XAttributeID) xid).getValue(), i);
			}

			tiles[i] = (Tile) node;
			tiles[i].setID(i);
		}

		XAttribute listDeps = trace.getAttributes().get("po:dependencies");
		if (listDeps != null && listDeps instanceof XAttributeList) {
			Collection<XAttribute> deps = ((XAttributeList) listDeps).getCollection();
			for (XAttribute source : deps) {
				XID sourceId = ((XAttributeID) source).getValue();
				XID targetId = ((XAttributeID) source.getAttributes().get("po:target"))
						.getValue();
				int sourceIndex = mapId2Index.get(sourceId);
				int targetIndex = mapId2Index.get(targetId);
//				ptrace.addDependency(DependencyFactory.createSimpleDirectDependency(sourceIndex, targetIndex), sourceIndex, targetIndex);

				dfg[sourceIndex][targetIndex] = true;

				POEdge e = new POEdgeImp(map.get(sourceIndex), map.get(targetIndex));
				graph.addEdge(e);
			}

		}
		boolean[][] efg = DAGraphImpl.computeEventuallyWarshall(dfg);
		boolean[][] newDfg = new boolean[efg.length][];
		for (int i = 0; i < efg.length; i++)
			newDfg[i] = efg[i].clone();

		for (int i = 0; i < efg.length; i++)
			newDfg[i][i] = false;

		int size = trace.size();
		// transitive reduction
		for (int j = 0; j < size; ++j) {
			for (int i = 0; i < size; ++i) {
				if (newDfg[i][j]) {
					for (int k = 0; k < size; ++k) {
						if (newDfg[j][k]) {
							newDfg[i][k] = false;
							POEdge edge = graph.getEdge(map.get(i), map.get(k));
							if (edge != null) {
								edge.setDirect(false);
							}
						}
					}
				}
			}
		}

		TilesPO tilesPO = new TilesPO(tiles, new Dimension(500, 500), trace);
		tilesPO.setDrawPanel(graph);

		DAGraph<Tile> dag = new DAGraphImpl<>(Arrays.asList(tiles), newDfg, efg);
		tilesPO.setDAGraph(dag);
		return tilesPO;
	}

	protected TilePOEventNode getTilePONode(int traceIndex, PartialOrderGraph graph, int i,
			XEvent event) {
		return new TilePOEventNode(traceIndex, graph, i, event);
	}



}
