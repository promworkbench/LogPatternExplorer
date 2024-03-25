package org.processmining.lip.model.pattern.vis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.processmining.lip.controller.TController;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.pattern.ContextPattern;
import org.processmining.lip.model.pattern.PatternInstances;
import org.processmining.lip.visualizer.TileChartMainview;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import gnu.trove.map.hash.THashMap;

public class PatternGraphVizView extends PatternInstanceView<Tile> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -740980602603597188L;

	private Dot dot;

	private DotPanel dotPanel;

	public class CenterDotNode extends DotNode {
		//transition
		public CenterDotNode(String label) {
			super(label, null);
			setOption("shape", "Mrecord");
			setOption("style", "round");
		}

		//tau transition
//		public LocalDotNode() {
//			super("", null);
//			setOption("style", "filled");
//			setOption("fillcolor", "#EEEEEE");
//			setOption("width", "0.15");
//			setOption("shape", "box");
//		}
	}

	public class ContextDotNode extends DotNode {
		public ContextDotNode(String label) {
			super(label, null);
			setOption("shape", "box");
		}
	}

	public PatternGraphVizView(TileChartMainview tileChartMainview,
			ContextPattern<TNode> p, PatternInstances<Tile> instances,
			List<String> patternInfo, TController controller) {
		super(tileChartMainview, p, instances, patternInfo, controller);
	}

	public Dot getDot() {
		return dot;
	}

	public void setDot(Dot dot) {
		this.dot = dot;
	}

	private static Map<String, String> getEventuallyCausesEdge() {
		Map<String, String> map = new THashMap<>();
		map.put("style", "dotted");
		return map;
	}

	protected JComponent _convertPatternToGraph(ContextPattern<TNode> p) {
		if (dot == null) {
			dot = new Dot();
		}
		dot.setDirection(GraphDirection.leftRight);

		Map<TNode, DotNode> map = new THashMap<>();
		for (TNode t : p.getGraph().getNodes()) {
			DotNode node = null;
			if (p.getCenter().equals(t)) {
				node = new CenterDotNode(t.getLabel());
			} else {
				node = new ContextDotNode(t.getLabel());
			}
			dot.addNode(node);
			map.put(t, node);
		}

		List<TNode> tiles = new ArrayList<TNode>(p.getGraph().getNodes());
		for (int i = 0; i < tiles.size(); i++) {
			TNode s = tiles.get(i);
			for (int j = i + 1; j < tiles.size(); j++) {
				TNode t = tiles.get(j);
//				POEdge e = null;
				if (p.getGraph().isDirectlyCauses(s, t)) {
					dot.addEdge(map.get(s), map.get(t));
				} else if (p.getGraph().isDirectlyCauses(t, s)) {
					dot.addEdge(map.get(t), map.get(s));
				} else if (p.getGraph().isEventuallyCauses(s, t)) {
					dot.addEdge(map.get(s), map.get(t), "", getEventuallyCausesEdge());
				} else if (p.getGraph().isEventuallyCauses(t, s)) {
					dot.addEdge(map.get(t), map.get(s), "", getEventuallyCausesEdge());
				}
			}
		}

		setDotPanel(new DotPanel(dot));

		return getDotPanel();
	}

	public void updatePatternColor(Color c) {
		if (dot == null) {
			return;
		}
		
		for (DotNode n : dot.getNodes()) {
			if (n instanceof CenterDotNode || n instanceof ContextDotNode) {
//				n.setOption("style", "filled");
//				n.setOption("fillcolor", ColorUtil.convertToHEXColor(c));
//				n.setOption("fontcolor", ColorUtil.convertToHEXColor(ColorUtil.getContrastColor(c)));
				
				n.setOption("color",  ColorUtil.convertToHEXColor(c));
			}
		}
		//TODO. 
		if (getDotPanel() != null) {
			getDotPanel().changeDot(dot, true);
//			dotPanel.setBackground(c);

		}
		getDotPanel().revalidate();
		getDotPanel().repaint();
	}

	private DotPanel getDotPanel() {
		return dotPanel;
	}

	private void setDotPanel(DotPanel dotPanel) {
		this.dotPanel = dotPanel;
	}

}
