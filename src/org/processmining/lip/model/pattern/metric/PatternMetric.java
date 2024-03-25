package org.processmining.lip.model.pattern.metric;

import java.util.Collection;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.lip.model.pattern.IPattern;
import org.processmining.lip.model.pattern.IPatternInstance;

public interface PatternMetric {

	public double computeMetric(XLog log, IPattern pattern,
			Collection<IPatternInstance> instances);
	
	public String computeMetricAsString(XLog log, IPattern pattern,
			Collection<IPatternInstance> instances);
	
	public String getMetricName();

	public PatternInfoField getFieldName();
	
	public XEventClassifier getClassifier();
	public void setClassifier(XEventClassifier c);
}
