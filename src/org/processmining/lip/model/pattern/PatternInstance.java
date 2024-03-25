package org.processmining.lip.model.pattern;

import java.util.List;

import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.Tiles;

public class PatternInstance<T> implements IPatternInstance {

	private Tiles tiles;
	private T center;
	private List<T> contextInstances;

	public PatternInstance(Tiles tiles, T center, List<T> contextInstances) {
		this.tiles = tiles;
		this.center = center;
		this.contextInstances = contextInstances;

	}

	public XTrace getTrace() {
		// TODO Auto-generated method stub
		return tiles.getTrace();
	}

	public T getCenter() {
		return this.center;
	}

	public List<T> getNodes() {
		return contextInstances;
	}
	
	public Tiles getTiles(){
		return tiles;
	}

}
