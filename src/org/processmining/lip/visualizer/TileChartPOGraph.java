package org.processmining.lip.visualizer;

import org.jgraph.event.GraphSelectionEvent;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.lip.controller.TController;
import org.processmining.lip.model.tilepo.TilePOEventNode;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.partialorder.models.graph.PONode;
import org.processmining.partialorder.models.graph.PartialOrderGraph;
import org.processmining.partialorder.plugins.vis.palignment.PGraphInfoPanel;
import org.processmining.partialorder.ptrace.plugins.vis.PTraceGraphPanel;

public class TileChartPOGraph extends PTraceGraphPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1983124576724328344L;
	private TController controller;

	public TileChartPOGraph(PluginContext context, PartialOrderGraph graph,
			PGraphInfoPanel poTraceInfoPanel) {
		super(context, graph, poTraceInfoPanel);
	}

	public TileChartPOGraph(PartialOrderGraph graph) {
		super(null, graph, null);
//		setMinimumSize(new Dimension(500, 200));
//		setPreferredSize(new Dimension(500, 200));

		for (PONode n : graph.getNodes()) {
			if (n instanceof TilePOEventNode) {
				((TilePOEventNode) n).setParent(comp);
			}
		}
	}
	
	@Override
	public void valueChanged(GraphSelectionEvent evt) {
	}

//	@Override
//	public void updateEventColor(Map<String, Color> mapEventToColor) {
//		comp.getModel().beginUpdate();
////		for (PONode n : graph.getNodes()) {
////			Color c = mapEventToColor.get(n.getLabel());
////			if (c != null) {
////				n.setColor(c);
////			}
////		}
//		comp.getModel().endUpdate();
//		comp.refresh();
//		comp.revalidate();
//		comp.repaint();
//
//	}

	public void setController(TController c) {
		this.controller = c;

	}

	public TilePOEventNode retrieveTileIndex(int coordX, int coordY) {
		Object cell = comp.getFirstCellForLocation(coordX, coordY);
		if (cell instanceof ProMGraphCell
				&& ((ProMGraphCell) cell).getNode() instanceof PONode) {
			TilePOEventNode node = (TilePOEventNode) ((ProMGraphCell) cell).getNode();
//			System.out.println(node.getLabel());

			return node;
		}
//		else {
////			controller.setOnEvent(null);
//			System.out.println(".");
//		}
		return null;

	}

	public ProMJGraph getComp() {
		return comp;
	}
}
