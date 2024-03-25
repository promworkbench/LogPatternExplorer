package org.processmining.lip.model.pattern.metric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.stat.inference.TTest;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.pattern.IPattern;
import org.processmining.lip.model.pattern.IPatternInstance;

import gnu.trove.set.hash.THashSet;

public class PatternMetricPIsignificance extends PatternMetricAbs {

	public double computeMetric(XLog log, IPattern pattern, Collection<IPatternInstance> instances) {
		List<Double> performance = new ArrayList<>();
		List<Double> performance2 = new ArrayList<>();
		Set<XTrace> patterntraces = new THashSet<>();
		for (IPatternInstance instance : instances) {
			XTrace trace = instance.getTrace();
			Long timedur = getTraceTimeDur(trace);
			performance.add(timedur.doubleValue());
			patterntraces.add(trace);
		}
		for (XTrace t : log) {
			if(!patterntraces.contains(t)) {
				Long timedur = getTraceTimeDur(t);
				performance2.add(timedur.doubleValue());
			}
		}
		TTest test = new TTest();
		System.out.println("new test ---------------");
		double pvalue = test.tTest(getDoubles(performance), getDoubles(performance2));
		return pvalue;
	}

	private double[] getDoubles(List<Double> performance) {
		double[] list = new double[performance.size()];
		System.out.println("new set start");
		for(int i = 0; i < performance.size(); i++) {
			list[i] = performance.get(i);
			System.out.println(performance.get(i));
		}
		System.out.println("new set end");
		return list;
	}

	private long getTraceTimeDur(XTrace trace) {
		return XTimeExtension.instance().extractTimestamp(trace.get(trace.size() - 1)).getTime()
				- XTimeExtension.instance().extractTimestamp(trace.get(0)).getTime();
	}

	public String computeMetricAsString(XLog log, IPattern pattern, Collection<IPatternInstance> instances) {
		double res = computeMetric(log, pattern, instances);
		System.out.println("p-value:" + res);
		return String.valueOf(res > 0.05);
	}

	public String getMetricName() {
		return "P-sigf";
	}

	public PatternInfoField getFieldName() {
		return PatternInfoField.P_sigf;
	}

	public String toString() {
		return getMetricName();
	}

}
