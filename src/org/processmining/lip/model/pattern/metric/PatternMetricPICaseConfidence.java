package org.processmining.lip.model.pattern.metric;

import java.text.DecimalFormat;
import java.util.Collection;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.pattern.IPattern;
import org.processmining.lip.model.pattern.IPatternInstance;

public class PatternMetricPICaseConfidence extends PatternMetricPICaseFrequency {

//	private XEventClassifier classifier = new XEventNameClassifier();
	DecimalFormat dff = new DecimalFormat("#.0");
	
	@Override
	public double computeMetric(XLog log, IPattern pattern,
			Collection<IPatternInstance> instances) {
		double num = super.computeMetric(log, pattern, instances);
		double totNum = 0.0;
		for (XTrace t : log) {
			for (XEvent e : t) {
				if (getClassifier().getClassIdentity(e).equals(pattern.getLabel())) {
					totNum++;
					break;
				}
			}
		}
		return num / totNum;
	}

	public String getMetricName() {
		return "C-conf";
	}
	
	public PatternInfoField getFieldName() {
		return PatternInfoField.C_conf;
	}
	public String computeMetricAsString(XLog log, IPattern pattern,
			Collection<IPatternInstance> instances) {
		double d = computeMetric(log, pattern, instances) * 100.0;
		return dff.format(d) + " %";
	}
}
