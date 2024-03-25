package org.processmining.lip.model.pattern.vis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.StringUtils;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.ui.widgets.ProMTextArea;
import org.processmining.lip.controller.TController;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.pattern.ContextPattern;
import org.processmining.lip.model.pattern.PatternInstance;
import org.processmining.lip.model.pattern.PatternInstances;
import org.processmining.lip.model.pattern.PatternToTextFactory;
import org.processmining.lip.visualizer.TileChartMainview;
import org.processmining.lip.visualizer.filter.PanelUtil;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.plugins.utils.ProvidedObjectHelper;

import com.fluxicon.slickerbox.components.SlickerButton;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public abstract class PatternInstanceView<T extends TNode> extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8551419778466491012L;

	private PluginContext context;

	private PatternInstances<T> patternInstances;

	boolean isVisTest = false;

	DotPanel dotPanel = null;

	public PatternInstanceView(final TileChartMainview tileChartMainview,
			final ContextPattern<TNode> p, final PatternInstances<T> instances,
			List<String> patternInfo, final TController controller) {
		super();
		this.patternInstances = instances;
//		setLayout(new BorderLayout());
		TitledBorder title = BorderFactory.createTitledBorder("Pattern");
		setBorder(title);
		title.setTitleColor(Color.WHITE);
		setPreferredSize(new Dimension(250, 200));
		setMinimumSize(new Dimension(250, 250));

		setForeground(Color.WHITE);
		setBackground(Color.DARK_GRAY);
//		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setLayout(new BorderLayout());
		Border border = BorderFactory.createEmptyBorder(0, 10, 0, 10);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.setForeground(Color.WHITE);
		infoPanel.setBackground(Color.DARK_GRAY);
		for (String info : patternInfo) {
			infoPanel.add(PanelUtil.createLabel(this, info, border));
		}
		add(infoPanel, BorderLayout.NORTH);

		if (isVisTest) {
			add(PanelUtil.createLabel(this, "Pattern center : " + p.getLabel(), border));
			add(PanelUtil.createLabel(this,
					"Pattern preds. : " + StringUtils.join(p.getPredecessors(), ", "),
					border));
			add(PanelUtil.createLabel(this,
					"Pattern concs. : " + StringUtils.join(p.getConcurrences(), ", "),
					border));
			add(PanelUtil.createLabel(this,
					"Pattern succs. : " + StringUtils.join(p.getSuccessors(), ", "),
					border));

		} else {
			JComponent g = _convertPatternToGraph(p);
			dotPanel = (DotPanel) g;

			add(g, BorderLayout.CENTER);
		}

		JPanel panelButtons = new JPanel();
		panelButtons.setForeground(Color.WHITE);
		panelButtons.setBackground(Color.DARK_GRAY);
//		panelButtons.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		SlickerButton bExport = new SlickerButton("Export");
//		add(PanelUtil.packLeftAligned(bExport));
		panelButtons.add(bExport);
		bExport.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				XLog sublog = XFactoryRegistry.instance().currentDefault().createLog();
				for (PatternInstance instance : patternInstances.getInstances()) {
					if (!sublog.contains(instance.getTrace())) {
						sublog.add(instance.getTrace());
					}
				}
				if (context != null) {
					ProvidedObjectHelper.publish(context, "Pattern log", sublog,
							XLog.class, false);
					ProvidedObjectHelper.setFavorite(context, sublog);
				}
			}

		});

		SlickerButton bAggregate = new SlickerButton("Aggr");
//		add(PanelUtil.packLeftAligned(bExport));
//		panelButtons.add(bAggregate);
		bAggregate.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Get name for pattern
				String s = "SubProcess";
				// Show the Pattern as Text to user. 
				ProMTextArea area = new ProMTextArea();
				area.setText(s);

				switch (JOptionPane.showConfirmDialog(null, new JScrollPane(area))) {
				case JOptionPane.OK_OPTION:
					s = area.getText();
					break;
				}

				aggregateLog(controller, s);
			}

		});

		SlickerButton buttonEditPattern = new SlickerButton("Edit");
//		add(PanelUtil.packLeftAligned(buttonEditPattern));
		panelButtons.add(buttonEditPattern);
		buttonEditPattern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = PatternToTextFactory
						.convertPatternToText((ContextPattern<TNode>) p);
				// Show the Pattern as Text to user. 
				ProMTextArea area = new ProMTextArea();
				area.setText(s);

				switch (JOptionPane.showConfirmDialog(null, new JScrollPane(area))) {
				case JOptionPane.OK_OPTION:
					s = area.getText();
					break;
				}
				// Parse edited string as pattern.
				ContextPattern<TNode> newPattern = PatternToTextFactory
						.convertTextToPattern(s);
				tileChartMainview.removePattern(p);
				tileChartMainview.extractPatternInstances(newPattern);
			}
		});

		SlickerButton buttonRemovePattern = new SlickerButton("Remove");
//		add(PanelUtil.packLeftAligned(buttonRemovePattern));
		panelButtons.add(buttonRemovePattern);
		buttonRemovePattern.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				tileChartMainview.removePattern(p);
			}
		});

		final JComponent panel = this;

		SlickerButton buttonColorPattern = new SlickerButton("Color");
//		add(PanelUtil.packLeftAligned(buttonColorPattern));
		panelButtons.add(buttonColorPattern);
		buttonColorPattern.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				Color newColor = JColorChooser.showDialog(panel,
						"Choose Background Color", null);
				if (newColor != null) {
					controller.setPatternColor(p, newColor);
					updatePatternColor(newColor);
					tileChartMainview.repaint();
				}
			}

		});

		SlickerButton buttonShow = new SlickerButton("focus");
		panelButtons.add(buttonShow);
		buttonShow.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				controller.setVisibleInstances(p);
				tileChartMainview.updateTileList();

			}
		});

		SlickerButton buttonAnti = new SlickerButton("Anti");
		panelButtons.add(buttonAnti);
		buttonAnti.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				controller.setVisibleAntiInstances(p);
				tileChartMainview.updateTileList();

			}
		});

		SlickerButton buttonRelabel = new SlickerButton("Relabel");
		panelButtons.add(buttonRelabel);
		buttonRelabel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String s = ((ContextPattern<TNode>) p).getLabel();
				// Show the Pattern as Text to user. 
				ProMTextArea area = new ProMTextArea();
				area.setText(s);

				switch (JOptionPane.showConfirmDialog(null, new JScrollPane(area),
						"Relabel events with ", JOptionPane.OK_CANCEL_OPTION)) {
				case JOptionPane.OK_OPTION:
					s = area.getText();
					break;
				}

				XLog log = controller.relabelEvents(p, s);
				if (context != null) {
					ProvidedObjectHelper.publish(context, "Relabeled log", log,
							XLog.class, false);

					ProvidedObjectHelper.setFavorite(context, log);
				}
//				tileChartMainview.updateTileList();

			}
		});

		add(PanelUtil.packLeftAligned(panelButtons), BorderLayout.SOUTH);

	}

	private void aggregateLog(final TController controller, String s) {
		// TODO Auto-generated method stub
		XLog parentLog = XFactoryRegistry.instance().currentDefault()
				.createLog(controller.getLog().getAttributes());
		XLog childLog = XFactoryRegistry.instance().currentDefault()
				.createLog(controller.getLog().getAttributes());

		Map<XTrace, List<PatternInstance>> map = new THashMap<>();

		for (PatternInstance instance : patternInstances.getInstances()) {

			if (!map.containsKey(instance.getTrace())) {
				map.put(instance.getTrace(), new ArrayList<PatternInstance>());
			}
			map.get(instance.getTrace()).add(instance);
		}

		for (XTrace t : controller.getLog()) {
			if (map.containsKey(t)) {

				Set<Pair<Integer, Integer>> list = getEventIntervals(map, t);

				// make abstract log and sub log. 
				XTrace parent = XFactoryRegistry.instance().currentDefault()
						.createTrace();
				int j = 0;
				while (j < t.size()) {
					Pair<Integer, Integer> interval = getInterval(list, j);

					if (interval != null) {
						// Create abstract event
						XEvent highEvent = (XEvent) t.get(j).clone();
						XConceptExtension.instance().assignName(highEvent, s);
						parent.add(highEvent);

						XTrace childTrace = XFactoryRegistry.instance().currentDefault()
								.createTrace();
						for (int i = interval.getFirst(); i < interval.getSecond()
								+ 1; i++) {
							childTrace.add(t.get(i));
						}
						childLog.add(childTrace);

						j = interval.getSecond() + 1;
					} else {
						parent.add((XEvent) t.get(j).clone());
						j++;
					}
				}
				parentLog.add(parent);
			} else {
				parentLog.add((XTrace) t.clone());
			}
		}
		if (context != null) {
			ProvidedObjectHelper.publish(context, "Parent log", parentLog, XLog.class,
					false);
			ProvidedObjectHelper.setFavorite(context, parentLog);
			ProvidedObjectHelper.publish(context, s + " log", childLog, XLog.class,
					false);
			ProvidedObjectHelper.setFavorite(context, childLog);
		}
	}

	private Pair<Integer, Integer> getInterval(Set<Pair<Integer, Integer>> list, int j) {
		for (Pair<Integer, Integer> pair : list) {
			if (pair.getFirst() <= j && j <= pair.getSecond()) {
				return pair;
			}
		}
		return null;
	}

	private Set<Pair<Integer, Integer>> getEventIntervals(
			Map<XTrace, List<PatternInstance>> map, XTrace t) {
		Set<Pair<Integer, Integer>> list = new THashSet<>();

		for (PatternInstance i : map.get(t)) {
			int start = ((Tile) i.getNodes().get(0)).getID();
			int end = ((Tile) i.getNodes().get(i.getNodes().size() - 1)).getID();
			for (Object n : i.getNodes()) {
				if (((Tile) n).getID() < start) {
					start = ((Tile) n).getID();
				}
				if (end < ((Tile) n).getID()) {
					end = ((Tile) n).getID();
				}
			}
			Iterator<Pair<Integer, Integer>> iterator = list.iterator();
			while (iterator.hasNext()) {
				Pair<Integer, Integer> pair = iterator.next();

				if (end < pair.getFirst() || pair.getSecond() < start) {
					// not overlapping
				} else {
					if (pair.getFirst() < start) {
						start = pair.getFirst();
					}
					if (pair.getSecond() > end) {
						end = pair.getSecond();
					}
					iterator.remove();
				}
			}
			if (start >= t.size() || end >= t.size()) {
				System.err.println("outrange");
			}
			list.add(new Pair<Integer, Integer>(start, end));
		}
		return list;
	}

	public abstract Dot getDot();

	protected abstract JComponent _convertPatternToGraph(ContextPattern<TNode> p);

	public abstract void updatePatternColor(Color newColor);

	public void setContext(PluginContext context) {
		this.context = context;
	}

}
