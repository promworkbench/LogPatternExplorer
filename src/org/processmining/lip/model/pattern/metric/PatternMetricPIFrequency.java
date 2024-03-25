package org.processmining.lip.model.pattern.metric;

import java.text.DecimalFormat;
import java.util.Collection;

import org.deckfour.xes.model.XLog;
import org.processmining.lip.model.pattern.IPattern;
import org.processmining.lip.model.pattern.IPatternInstance;

public class PatternMetricPIFrequency extends PatternMetricAbs {

	DecimalFormat df = new DecimalFormat("#");

	public double computeMetric(XLog log, IPattern pattern,
			Collection<IPatternInstance> instances) {

		return instances.size();
	}

	public String getMetricName() {
		return "P-supp";
	}

	public PatternInfoField getFieldName() {
		return PatternInfoField.P_supp;
	}

	public String toString() {
		return getMetricName();
	}

	public String computeMetricAsString(XLog log, IPattern pattern,
			Collection<IPatternInstance> instances) {

		return df.format(computeMetric(log, pattern, instances));
	}

}
