package org.processmining.lip.model.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.processmining.lip.model.DAGraph;
import org.processmining.lip.model.TNode;

@Deprecated
public class ContextPatternImpl<T  extends TNode> implements ContextPattern<T> {
	String label;
	List<String> preds;
	List<String> succs;
	List<String> concurs;

	public ContextPatternImpl() {
		preds = new ArrayList<>();
		succs = new ArrayList<>();
		concurs = new ArrayList<>();
	}

	public ContextPatternImpl(String label, List<String> preds, List<String> concurs,
			List<String> succs) {
		this.label = label;
		this.preds = preds;
		this.concurs = concurs;
		this.succs = succs;
	}

	public String getLabel() {
		return label;
	}

	public List<String> getPredecessors() {
		return preds;
	}

	public List<String> getConcurrences() {
		return concurs;
	}

	public List<String> getSuccessors() {
		return succs;
	}

	public Map<String, String> getAttrToValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString() {
		return label + ";" + StringUtils.join(preds, ",") + ";"
				+ StringUtils.join(concurs, ",") + ";" + StringUtils.join(succs, ",");
	}

	public DAGraph<T> getGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	public T getCenter() {
		// TODO Auto-generated method stub
		return null;
	}


}
