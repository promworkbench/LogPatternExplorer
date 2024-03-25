package org.processmining.lip.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class DAGraphImpl<T extends TNode> implements DAGraph<T> {

	boolean[][] directlyCauses;
	boolean[][] eventuallyCauses;
	BiMap<T, Integer> mapT2Idx;

	/**
	 * This constructor create a directed acyclic graph and deduce eventually
	 * follows relation from directlyCauses
	 * 
	 * @param collection
	 * @param directlyCauses
	 */
	public DAGraphImpl(List<T> collection, boolean[][] directlyCauses) {
		this.directlyCauses = directlyCauses;

		this.eventuallyCauses = computeEventuallyWarshall(directlyCauses);

		mapT2Idx = HashBiMap.create(collection.size());
		for (int i = 0; i < collection.size(); i++) {
			mapT2Idx.put(collection.get(i), i);
		}

	}

	public DAGraphImpl(List<T> collection, boolean[][] directlyCauses,
			boolean[][] eventuallyCauses) {
		this.directlyCauses = directlyCauses;
		this.eventuallyCauses = eventuallyCauses;

		this.eventuallyCauses = computeEventuallyWarshall(eventuallyCauses);

		mapT2Idx = HashBiMap.create(collection.size());
		for (int i = 0; i < collection.size(); i++) {
			mapT2Idx.put(collection.get(i), i);
		}

	}

	/**
	 * Creates a new DAGraph, projects this DAGraph on the set of nodes, and
	 * copies directly-follows and eventually-follows relations of the nodes.
	 */
	public DAGraph<T> projectOn(List<T> nodes) {
		boolean[][] directlyCauses = new boolean[nodes.size()][nodes.size()];
		boolean[][] eventuallyCauses = new boolean[nodes.size()][nodes.size()];

		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.size(); j++) {
				directlyCauses[i][j] = this.directlyCauses[mapT2Idx
						.get(nodes.get(i))][mapT2Idx.get(nodes.get(j))];
				eventuallyCauses[i][j] = this.eventuallyCauses[mapT2Idx
						.get(nodes.get(i))][mapT2Idx.get(nodes.get(j))];
			}
		}
		return new DAGraphImpl<T>(nodes, directlyCauses, eventuallyCauses);
	}

	public static boolean[][] computeEventuallyWarshall(boolean[][] directlyCauses) {
		int size = directlyCauses.length;
		boolean[][] newEventuallyCauses = new boolean[size][];
		for (int i = 0; i < size; i++)
			newEventuallyCauses[i] = directlyCauses[i].clone();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (newEventuallyCauses[j][i]) {
					for (int k = 0; k < size; k++) {
						if (newEventuallyCauses[j][i] && newEventuallyCauses[i][k]) {
							newEventuallyCauses[j][k] = true;
						}
					}
				}
			}
		}
//		eventuallyCauses = newEventuallyCauses;
		return newEventuallyCauses;
	}

	public Set<T> getNodes() {
		return mapT2Idx.keySet();
	}

	public List<T> getConcurrentNodes(T node) {
		int index = _getIndex(node);

		List<T> concurs = new ArrayList<>();
		for (int i = 0; i < this.eventuallyCauses.length; i++) {
			if (!this.eventuallyCauses[i][index] && !this.eventuallyCauses[index][i]
					&& i != index) {
				concurs.add(_getNode(i));
			}
		}
		return concurs;
	}

	public List<T> getDirectlyPredecessors(T node) {
		int index = _getIndex(node);

		List<T> preds = new ArrayList<>();
		for (int i = 0; i < this.directlyCauses.length; i++) {
			if (this.directlyCauses[i][index]) {
				preds.add(_getNode(i));
			}
		}
		return preds;
	}

	public List<T> getEventuallyPredecessors(T node) {
		int index = _getIndex(node);

		List<T> preds = new ArrayList<>();
		for (int i = 0; i < this.eventuallyCauses.length; i++) {
			if (this.eventuallyCauses[i][index]) {
				preds.add(_getNode(i));
			}
		}
		return preds;
	}

	public List<T> getDirectlySuccessors(T node) {
		int index = _getIndex(node);

		List<T> succs = new ArrayList<>();
		for (int i = 0; i < this.directlyCauses.length; i++) {
			if (this.directlyCauses[index][i]) {
				succs.add(_getNode(i));
			}
		}
		return succs;
	}

	public List<T> getEventuallySuccessors(T node) {
		int index = _getIndex(node);

		List<T> succs = new ArrayList<>();
		for (int i = 0; i < this.eventuallyCauses.length; i++) {
			if (this.eventuallyCauses[index][i]) {
				succs.add(_getNode(i));
			}
		}
		return succs;
	}

	public boolean isDirectlyCauses(T source, T target) {
		return directlyCauses[_getIndex(source)][_getIndex(target)];
	}

	public boolean isEventuallyCauses(T source, T target) {
		return eventuallyCauses[_getIndex(source)][_getIndex(target)];
	}

	private T _getNode(int i) {
		return mapT2Idx.inverse().get(i);
	}

	private Integer _getIndex(T node) {
		return mapT2Idx.get(node);
	}

}
