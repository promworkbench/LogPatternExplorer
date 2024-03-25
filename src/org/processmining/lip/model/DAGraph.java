package org.processmining.lip.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface DAGraph<N extends TNode> {
	
	Set<N> getNodes();
	
	
	List<N> getConcurrentNodes(N node);
	
	List<N> getDirectlyPredecessors(N node);
	List<N> getEventuallyPredecessors(N node);

	List<N> getDirectlySuccessors(N node);
	List<N> getEventuallySuccessors(N node);
	
	boolean isDirectlyCauses(N source, N target);
	boolean isEventuallyCauses(N source, N target);

	DAGraph<N> projectOn(List<N> nodes);
	
	
}
