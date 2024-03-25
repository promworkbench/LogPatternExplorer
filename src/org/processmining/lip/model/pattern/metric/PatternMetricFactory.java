package org.processmining.lip.model.pattern.metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.pattern.IPattern;
import org.processmining.lip.model.pattern.IPatternInstance;
import org.processmining.lip.model.pattern.PatternInstances;

import com.google.common.collect.Multiset;

import gnu.trove.map.hash.THashMap;

public class PatternMetricFactory<T> {

//	public static List<String> computeMetrics(XLog log, IPattern pattern,
//			PatternInstances<Tile> instances, Map<String, Multiset<String>> map) {
//		Map<PatternInfoField, String> mapRes = computeMetricsMap(log, pattern, instances,
//				map);
//		List<String> res = new ArrayList<String>();
//		for (Entry<PatternInfoField, String> info : mapRes.entrySet()) {
//			res.add(info.getKey() + " : " + info.getValue());
//		}
//
//		return res;
//	}

	public static Map<PatternInfoField, String> computeMetricsMap(XLog log,
			IPattern pattern, PatternInstances<Tile> instances,
			Map<String, Multiset<String>> map, XEventClassifier xEventClassifier) {
		List<PatternMetric> metrics = new ArrayList<>();
		Map<PatternInfoField, String> mapRes = new THashMap<>();

		
		metrics.add(new PatternMetricPIFrequency());
		metrics.add(new PatternMetricPIConfidence());
		metrics.add(new PatternMetricPICaseFrequency());
		metrics.add(new PatternMetricPICasePercentage());
		metrics.add(new PatternMetricPICaseConfidence());
		
		//TODO: testing...
//		metrics.add(new PatternMetricPIsignificance());

//		DecimalFormat df = new DecimalFormat("#.000"); 

		for (PatternMetric m : metrics) {
			m.setClassifier(xEventClassifier);
			String d = m.computeMetricAsString(log, pattern,
					new ArrayList<IPatternInstance>(instances.getInstances()));
			mapRes.put(m.getFieldName(), d);
//			res.add(m.getMetricName() + " : " + d);
		}
		return mapRes;
	}

}
