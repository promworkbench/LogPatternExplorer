package org.processmining.lip.model.pattern.metric;

import java.text.DecimalFormat;
import java.util.Collection;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.pattern.IPattern;
import org.processmining.lip.model.pattern.IPatternInstance;

public class PatternMetricPIConfidence extends PatternMetricAbs {

	private DecimalFormat df = new DecimalFormat("#.0");

//	private Multiset<String> labelFrequency;
	public PatternMetricPIConfidence() {

	}
//	public PatternMetricPIConfidence(Multiset<String> labelFrequency) {
//		this.labelFrequency = labelFrequency;
//	}

	public double computeMetric(XLog log, IPattern pattern,

			Collection<IPatternInstance> instances) {
		int totNum = 0;
		for (XTrace t : log) {
			for (XEvent e : t) {
				String s = getClassifier().getClassIdentity(e);
				if (s!= null && s.equals(pattern.getLabel())) {
					totNum++;
				}
			}
		}

//		double d = (double) instances.size() / (double) labelFrequency.count(pattern.getLabel());
		double d = (double) instances.size() / totNum;
		return d;
	}

	public String getMetricName() {
		return "P-conf";
	}

	public PatternInfoField getFieldName() {
		return PatternInfoField.P_conf;
	}

	public String computeMetricAsString(XLog log, IPattern pattern,
			Collection<IPatternInstance> instances) {
		double d = computeMetric(log, pattern, instances) * 100.0;
		return df.format(d) + " %";
	}

}
