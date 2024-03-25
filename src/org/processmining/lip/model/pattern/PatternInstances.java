package org.processmining.lip.model.pattern;

import java.util.List;
import java.util.Map;

import org.processmining.lip.model.pattern.metric.PatternInfoField;

public class PatternInstances<T> {

	List<PatternInstance<T>> instances;
	List<PatternInstance<T>> antiInstances;
	private Map<PatternInfoField, String> res;





	public List<PatternInstance<T>> getInstances() {
		return instances;
	}

	public void setInstances(List<PatternInstance<T>> instances) {
		this.instances = instances;
	}

	public List<PatternInstance<T>> getAntiInstances() {
		return antiInstances;
	}

	public void setAntiInstances(List<PatternInstance<T>> antiInstances) {
		this.antiInstances = antiInstances;
	}

	public void setInfo(Map<PatternInfoField, String> res) {
		this.res = res;
	}
	public Map<PatternInfoField, String> getInfo() {
		return res;
	}
}
