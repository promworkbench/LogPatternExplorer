package org.processmining.lip.model.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.DAGraphImpl;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.TNodeImpl;

import gnu.trove.map.hash.THashMap;

public class PatternToTextFactory {

	private static final String EDGE_TYPE_EF = "EF";
	private static final String EDGE_TYPE_DF = "DF";
	private static final String CLOSE_BRACKET = ")";
	private static final String OPEN_BRACKET = "(";
	private static final String ATTR_SEPARATOR = ", ";
	private static final String ELEMENT_SEPARATOR = "; ";
	private static final String LINE_BREAK = "\n";
	private static final String KEY_CENTER = "center:";
	private static final String KEY_NODES = "nodes:";
	private static final String KEY_EDGES = "edges:";
	private static final String EDGE_ARC = " -> ";

	public static class TEdge {
		public int sourceId;
		public int targetId;
		public String type;

		public TEdge() {
		};
	}

	public static String convertPatternToText(ContextPattern<TNode> p) {
		String s = "";
		s += KEY_CENTER + LINE_BREAK;
		s += getNodeString(p.getCenter()) + LINE_BREAK;

		s += KEY_NODES + LINE_BREAK;
		for (TNode n : p.getGraph().getNodes()) {
			s += getNodeString(n) + ELEMENT_SEPARATOR + LINE_BREAK;
		}

		s += KEY_EDGES + LINE_BREAK;
		for (TNode n : p.getGraph().getNodes()) {
			for (TNode t : p.getGraph().getNodes()) {
				if (p.getGraph().isDirectlyCauses(n, t)) {
					s += getEdgeString(n, t, EDGE_TYPE_DF) + ELEMENT_SEPARATOR
							+ LINE_BREAK;
				}
				if (p.getGraph().isEventuallyCauses(n, t)) {
					s += getEdgeString(n, t, EDGE_TYPE_EF) + ELEMENT_SEPARATOR
							+ LINE_BREAK;
				}
			}
		}
		System.out.println(s);
		return s;
	}

	public static ContextPattern<TNode> convertTextToPattern(String newString) {
		List<String> values = Arrays.asList(newString.split("\\r?\\n"));

		List<TNode> nodes = new ArrayList<TNode>();
		Map<Integer, Integer> mapID2Index = new THashMap<>();

		int index = values.indexOf(KEY_CENTER);
		TNode center = parseNode(values.get(++index));

		index = values.indexOf(KEY_NODES);
		index++;
		int endIndex = values.indexOf(KEY_EDGES);
		for (int i = index; i < endIndex; i++) {
			TNode node = parseNode(values.get(i));
			nodes.add(node);
			mapID2Index.put(node.getID(), nodes.size() - 1);
		}
		center = nodes.get(mapID2Index.get(center.getID()));
		if (!nodes.contains(center)) {
			System.err.println("Center not contained in nodes.");
		}
		boolean[][] directlyCauses = new boolean[nodes.size()][nodes.size()];
		boolean[][] eventuallyCauses = new boolean[nodes.size()][nodes.size()];

		index = values.indexOf(KEY_EDGES);
		index++;
		for (int i = index; i < values.size(); i++) {
			TEdge edge = parseEdge(values.get(i));
			if (edge.type.equals(EDGE_TYPE_DF)) {
				directlyCauses[mapID2Index.get(edge.sourceId)][mapID2Index
						.get(edge.targetId)] = true;
				eventuallyCauses[mapID2Index.get(edge.sourceId)][mapID2Index
						.get(edge.targetId)] = true;
			} else if (edge.type.equals(EDGE_TYPE_EF)) {
				eventuallyCauses[mapID2Index.get(edge.sourceId)][mapID2Index
						.get(edge.targetId)] = true;
			}
		}
		

		DAGraph<TNode> graph = new DAGraphImpl<>(nodes, directlyCauses, eventuallyCauses);
		ContextPattern<TNode> pattern = new ContextPatternGraphImpl(center, graph);
//		for (String s : values) {
//			System.out.println(s);
//		}
//		System.out.println("Converted");
		System.out.println(convertPatternToText(pattern));
		return pattern;
	}

	private static TEdge parseEdge(String string) {
		int indexSourceId = string.indexOf(OPEN_BRACKET) + 1;
		int indexSourceEndId = string.indexOf(EDGE_ARC);

		int indexTargetId = indexSourceEndId + EDGE_ARC.length();
		int indexTargetEndId = string.indexOf(ATTR_SEPARATOR);

		int indexType = indexTargetEndId + 2;
		int indexTypeEnd = string.indexOf(CLOSE_BRACKET);

		TEdge e = new TEdge();
		e.sourceId = Integer.parseInt(string.substring(indexSourceId, indexSourceEndId));
		e.targetId = Integer.parseInt(string.substring(indexTargetId, indexTargetEndId));
		e.type = string.substring(indexType, indexTypeEnd);
		return e;
	}

	private static TNode parseNode(String string) {
		int indexId = string.indexOf(OPEN_BRACKET) + 1;
		int indexEndId = string.indexOf(ATTR_SEPARATOR);
		int indexStartLabel = indexEndId + 2;
		int indexEndLabel = string.indexOf(CLOSE_BRACKET);
		TNode n = new TNodeImpl(Integer.parseInt(string.substring(indexId, indexEndId)),
				string.substring(indexStartLabel, indexEndLabel));
		return n;
	}

	private static String getEdgeString(TNode n, TNode t, String string) {
		return addParenthesis(n.getID() + EDGE_ARC + t.getID() + ATTR_SEPARATOR + string);
	}

	private static String getNodeString(TNode center) {
		return addParenthesis(center.getID() + ATTR_SEPARATOR + center.getLabel());
	}

	private static String addParenthesis(String label) {
		return OPEN_BRACKET + label + CLOSE_BRACKET;
	}

}
