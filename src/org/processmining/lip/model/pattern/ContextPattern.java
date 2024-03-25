package org.processmining.lip.model.pattern;

import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.TNode;

public interface ContextPattern<T extends TNode> extends IPattern {

	public DAGraph<T> getGraph();

	public T getCenter();

	//	Collection<N> getConcurrentNodes(N node);
	//	
	//	Collection<N> getDirectlyPredecessors(N node);
	//	Collection<N> getEventuallyPredecessors(N node);
	//
	//	Collection<N> getDirectlySuccessors(N node);
	//	Collection<N> getEventuallySuccessors(N node);

}
