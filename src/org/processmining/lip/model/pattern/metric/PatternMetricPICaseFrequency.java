package org.processmining.lip.model.pattern.metric;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.pattern.IPattern;
import org.processmining.lip.model.pattern.IPatternInstance;

import gnu.trove.set.hash.THashSet;

public class PatternMetricPICaseFrequency extends PatternMetricAbs {

	DecimalFormat df = new DecimalFormat("#");

	public double computeMetric(XLog log, IPattern pattern,
			Collection<IPatternInstance> instances) {
		Set<XTrace> traces = new THashSet<>();
		for (IPatternInstance i : instances) {
			traces.add(i.getTrace());
		}
		return traces.size();
	}

	public String getMetricName() {
		return "C-supp";
	}

	public String computeMetricAsString(XLog log, IPattern pattern,
			Collection<IPatternInstance> instances) {
		double d = computeMetric(log, pattern, instances);

		return df.format(d);
	}

	public PatternInfoField getFieldName() {
		return PatternInfoField.C_supp;
	}

}
