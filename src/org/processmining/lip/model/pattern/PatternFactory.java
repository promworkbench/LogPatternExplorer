package org.processmining.lip.model.pattern;

import java.util.List;

import org.processmining.lip.model.TNode;

public interface PatternFactory<TS extends Iterable<T>, T> {

	public ContextPattern<TNode> fitTilesToPattern(List<T> tiles, TS tileTrace);

//	public ContextPattern fitEventsToPattern(List<XEvent> events);

//	public List<PatternInstance> matchEventToPattern(XLog log, ContextPattern pattern);

	public PatternInstances<T> matchEventToPattern(List<TS> allTiles,
			ContextPattern<TNode> p);

}
