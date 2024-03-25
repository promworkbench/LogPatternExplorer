package org.processmining.lip.model.pattern.metric;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;

public abstract class PatternMetricAbs implements PatternMetric {

	private XEventClassifier classifier = new XEventNameClassifier();

	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		if (classifier != null) {
			this.classifier = classifier;
		}
	}

}
