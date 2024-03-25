package org.processmining.lip.model.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.TNode;

public class ContextPatternGraphImpl implements ContextPattern<TNode> {

	private TNode center;
	private DAGraph<TNode> graph;

	public ContextPatternGraphImpl(TNode center, DAGraph<? extends TNode> projected) {
		this.center = center;
		this.graph = (DAGraph<TNode>) projected;
	}

	public String getLabel() {
		return center.getLabel();
	}

	public List<String> getPredecessors() {
		Collection<TNode> nodes = graph.getEventuallyPredecessors(center);
		return _toLabels(nodes);
	}

	public List<String> getConcurrences() {
		Collection<TNode> nodes = graph.getConcurrentNodes(center);
		return _toLabels(nodes);
	}

	public List<String> getSuccessors() {
		Collection<TNode> nodes = graph.getEventuallySuccessors(center);
		return _toLabels(nodes);
	}

	private List<String> _toLabels(Collection<TNode> nodes) {
		List<String> labels = new ArrayList<>();
		for (TNode t : nodes) {
			labels.add(t.getLabel());
		}
		return labels;
	}

	public DAGraph<TNode> getGraph() {
		return graph;
	}

	public TNode getCenter() {
		return center;
	}

}
