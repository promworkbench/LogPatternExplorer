package org.processmining.lip.visualizer;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

public class TileChartVisualizer {


	@Visualizer
	@Plugin(name = "Log Pattern Explorer", returnLabels = { "vis" }, returnTypes = {
			JComponent.class }, parameterLabels = { "Event log" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "x.lu", email = "x.lu@tue.nl")
	@PluginVariant(variantLabel = "vis", requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, XLog log) {
		TileChartMainview view = new TileChartMainview(log);
		view.setContext(context);
		return view;
	}
	
}
