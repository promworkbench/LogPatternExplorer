package org.processmining.lip.controller;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.TileDEFAULT;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.model.pattern.ContextPattern;
import org.processmining.lip.model.pattern.PatternFactory;
import org.processmining.lip.model.pattern.PatternFactoryGraphImpl;
import org.processmining.lip.model.pattern.PatternInstance;
import org.processmining.lip.model.pattern.PatternInstances;
import org.processmining.lip.visualizer.TileChartMainview;
import org.processmining.lip.visualizer.filter.AttrParser;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public class TController {

	private TileLayout layout;

	private TileLayout[] allPossibleLayout;

	private XLog log;
//	private XLogInfo logInfo;
	Map<String, Multiset<String>> mapAttrkey2value;

	private Tiles[] tss;

	private TileChartMainview view;

	// Draw info panel
	private XEvent onEvent = null;
	private XTrace onTrace;
	private Tile onTile;

	private Tiles selectedTileTrace;
	private List<Tile> selectedTiles = new ArrayList<>();
	private Set<ContextPattern<TNode>> selectedPatterns;

	private Map<ContextPattern<TNode>, PatternInstances<Tile>> mapContext2PatternInstances;
	private Map<ContextPattern, Color> mapPattern2Color;

	private int colorSimilarityThreshold;

	private PatternFactory<Tiles, Tile> factory = new PatternFactoryGraphImpl();

	private XEventClassifier classifier;

	private boolean isShowAnti;

	public TController(TileChartMainview view, XLog log) {
		this.log = log;
		this.tss = new Tiles[log.size()];

		this.view = view;

		this.selectedPatterns = new THashSet<>();

		preprocessingLogForStatistics(log);
		XLogInfo logInfo = XLogInfoFactory.createLogInfo(log);

//		XAttributeInfo attrInfo = logInfo.getEventAttributeInfo();
		List<TileLayout> listLayouts = new ArrayList<>();
		listLayouts.add(TileLayoutSequential.getInstance());

		if (mapAttrkey2value.containsKey(XTimeExtension.KEY_TIMESTAMP)) {
			TileLayoutTimeAbs timeAbsLayout = new TileLayoutTimeAbs(
					logInfo.getLogTimeBoundaries().getStartDate(),
					logInfo.getLogTimeBoundaries().getEndDate(), 2000);

			listLayouts.add(TileLayoutStack.getInstance());
			listLayouts.add(timeAbsLayout);
			layout = listLayouts.get(1);
		} else {
			layout = listLayouts.get(0);
		}
		if (mapAttrkey2value.containsKey("po:id")) {
			listLayouts.add(TileLayoutPOTs.getInstance());

			if (mapAttrkey2value.containsKey("alignment:movetype")) {
				listLayouts.add(TileLayoutPOAs.getInstance());
			}
		}

		allPossibleLayout = listLayouts.toArray(new TileLayout[listLayouts.size()]);

		for (int i = 0; i < log.size(); i++) {
			XTrace t = log.get(i);
			computeTile(i, t);
		}

		mapContext2PatternInstances = new THashMap<ContextPattern<TNode>, PatternInstances<Tile>>();
		mapPattern2Color = new THashMap<ContextPattern, Color>();
	}

	private void preprocessingLogForStatistics(XLog log) {
		mapAttrkey2value = new THashMap<>();
		for (XTrace t : log) {
			for (XEvent e : t) {
				for (Entry<String, XAttribute> a : e.getAttributes().entrySet()) {
					if (!mapAttrkey2value.containsKey(a.getKey())) {
						Multiset<String> s = HashMultiset.create();
						mapAttrkey2value.put(a.getKey(), s);
					}
					mapAttrkey2value.get(a.getKey()).add(a.getValue().toString());
				}
			}
		}
	}

	public Map<String, Multiset<String>> getMapKeyToValue() {
		return mapAttrkey2value;
	}

	public void setMap(Map<String, Multiset<String>> map) {
		this.mapAttrkey2value = map;
	}

	public void setTileLayout(TileLayout selectedItem) {
		layout = selectedItem;
		fireTileLayoutChange();
	}

	public TileLayout getTileLayout() {
		return layout;
	}

	public TileLayout[] getAllTileLayouts() {
		return allPossibleLayout;
	}

	public List<Tiles> getListOfTilesVisible() {
		List<Tiles> tss = new ArrayList<>();

		if (selectedPatterns.isEmpty()) {
			// Show all
			for (int i = 0; i < log.size(); i++) {
				Tiles ts = getTiles(i);
				tss.add(ts);
			}

		} else {
			Set<Integer> traceIndex = new THashSet<Integer>();
			for (ContextPattern pattern : selectedPatterns) {
				PatternInstances<Tile> instances = this.getInstancesOfPattern(pattern);
				List<PatternInstance<Tile>> instanceList = new ArrayList<>();
				if (!isShowAnti) {
					instanceList = instances.getInstances();
				} else {
					instanceList = instances.getAntiInstances();

				}
				for (PatternInstance<Tile> instance : instanceList) {
					if (!traceIndex.contains(instance.getTiles().getTraceIndex())) {
						tss.add(instance.getTiles());
						traceIndex.add(instance.getTiles().getTraceIndex());
					}
				}
			}
		}
		return tss;
	}

	public Tiles getTiles(int trcIndex) {
		return getTiles(trcIndex, this.log.get(trcIndex));
	}

	public Tiles getTiles(int trcIndex, XTrace trace) {
		if (this.tss[trcIndex] == null) {
			computeTile(trcIndex, trace);
		}
		return this.tss[trcIndex];
	}

	public List<Tiles> getAllTiles() {
		return Arrays.asList(tss);
	}

	private void computeTile(int trcIndex, XTrace trace) {
		// Compute tiles
		Tiles ts = this.getTileLayout().computeTiles(trace, view.getTileSize());
		ts.setTraceIndex(trcIndex);
		this.tss[trcIndex] = ts;
	}

	public XLog getLog() {
		return log;
	}

	// TODO: currently only return the traces that contains an event that is 
	// relabeled. The log should contain all traces.
	public XLog relabelEvents(ContextPattern<TNode> p, String s) {
		XFactory currentDefault = XFactoryRegistry.instance().currentDefault();
		XLog newLog = currentDefault.createLog(getLog().getAttributes());
//		newLog.setAttributes(getLog().getAttributes());

		newLog.addAll(getLog());

		for (PatternInstance<Tile> instance : this.getInstancesOfPattern(p)
				.getInstances()) {
			XEvent e = instance.getTrace().get(instance.getCenter().getID());
			e.getAttributes().put("original:label", currentDefault.createAttributeLiteral(
					"original:label", XConceptExtension.instance().extractName(e), null));
			XConceptExtension.instance().assignName(e, s);
			newLog.set(instance.getTiles().getTraceIndex(), instance.getTrace());
		}
//		for (int i = 0; i < getLog().size(); i++) {
//			if(newLog.get(i)==null){
//				traces.set(i, getLog().get(i));
//			}
//		}
//		newLog.addAll(traces);
		// TODO: Add other traces to the relabeled log. 
		return newLog;
	}

	public void setColorSimilarThreshold(int threshold) {
		this.colorSimilarityThreshold = threshold;
		//TODO: fire color changed
	}

	public void setColorMap(String attrkey, Map<String, Color> colorMap) {
		Map<String, Color> newMap = new THashMap<>(colorMap);

		/*
		 * This is for trying color "similar values" similarly. 
		 * But is not working because distances is difficult to compute... 
		 */
//		Set<String> otherValues = this.mapAttrkey2value.get(attrkey).elementSet();
//		for (String v : otherValues) {
//			if (colorMap.containsKey(v))
//				continue;
//			// else
//
//			for (Entry<String, Color> entry : colorMap.entrySet()) {
//				String coloredKey = entry.getKey();
//				int dist = StringUtils.getLevenshteinDistance(v, coloredKey);
//				System.out
//						.println("StringUtil : " + v + ", " + coloredKey + " : " + dist);
//
//				if (dist < colorSimilarityThreshold) {
//					Color transformed = transColor(entry.getValue(), dist,
//							colorSimilarityThreshold);
//					newMap.put(v, transformed);
//				}
//			}
//		}

		/*
		 * This is the actual coloring. 
		 */
		for (int trcIndex = 0; trcIndex < tss.length; trcIndex++) {
			Tiles ts = tss[trcIndex];
			XTrace trc = log.get(trcIndex);
			for (int evtIndex = 0; evtIndex < ts.getTiles().length; evtIndex++) {
				Tile t = ts.getTiles()[evtIndex];

				XEvent e = trc.get(evtIndex);
				XAttribute attr = e.getAttributes().get(attrkey);
				if (attr != null) {
					Color c = newMap.get(attr.toString());
					if (c != null) {
						t.setColor(c);
					} else {
						// Reset the color of the rest tiles, including pattern color 
						t.setColor(TileDEFAULT.TILE_DEFAULT_FILL_COLOR);
					}
				}
			}
		}

		this.view.updateTileList();
	}

	public void resetColorMap() {

		for (int trcIndex = 0; trcIndex < tss.length; trcIndex++) {
			Tiles ts = tss[trcIndex];
			XTrace trc = log.get(trcIndex);

			for (int evtIndex = 0; evtIndex < ts.getTiles().length; evtIndex++) {
				Tile t = ts.getTiles()[evtIndex];
				// Reset the color of the rest tiles, including pattern color 
				t.setColor(TileDEFAULT.TILE_DEFAULT_FILL_COLOR);

			}
		}

		this.view.updateTileList();
	}

	private Color transColor(Color value, int dist, int max) {
		int red = value.getRed();
		int blue = value.getBlue();
		int green = value.getGreen();

		int defaultRed = TileDEFAULT.TILE_DEFAULT_FILL_COLOR.getRed();
		int defaultBlue = TileDEFAULT.TILE_DEFAULT_FILL_COLOR.getBlue();
		int defaultGreen = TileDEFAULT.TILE_DEFAULT_FILL_COLOR.getGreen();

		double v = (double) dist / max;

		int newRed = (int) ((red - defaultRed) * v + defaultRed);
		int newBlue = (int) ((blue - defaultBlue) * v + defaultBlue);
		int newGreen = (int) ((green - defaultGreen) * v + defaultGreen);
		return new Color(newRed, newGreen, newBlue);
	}

	public void setParserColorMap(String key, Map<AttrParser, Color> map) {
		for (int trcIndex = 0; trcIndex < tss.length; trcIndex++) {
			Tiles ts = tss[trcIndex];
			XTrace trc = log.get(trcIndex);

			for (int evtIndex = 0; evtIndex < ts.getTiles().length; evtIndex++) {
				Tile t = ts.getTiles()[evtIndex];

				XEvent e = trc.get(evtIndex);
//				XAttribute attr = e.getAttributes().get(key);
//				
				for (Entry<AttrParser, Color> entry : map.entrySet()) {
					XAttribute attr = e.getAttributes().get(entry.getKey().getAttrKey());
					if (attr != null) {
						Pattern p = Pattern
								.compile(entry.getKey().getAttrRegPatternParser());
						Matcher m = p.matcher(attr.toString());

						if (m.find()) {
							t.setColor(entry.getValue());
						}
					}
				}
//					Color c = colorMap.get();
//				}
			}
		}
		this.view.updateTileList();

	}

	public void fireTileLayoutChange() {
		for (int trcIndex = 0; trcIndex < tss.length; trcIndex++) {
			computeTile(trcIndex, this.log.get(trcIndex));
		}
	}

	public XEvent getOnEvent() {
		return onEvent;
	}

	public void setOnEvent(XEvent onEvent) {
		this.onEvent = onEvent;
		this.view.repaint();
	}

	public XTrace getOnTrace() {
		return onTrace;
	}

	public void setOnTrace(XTrace trace) {
		this.onTrace = trace;
		this.view.repaint();
	}

//	public List<String> getInfo() {
//		if (onEvent == null) {
//			return null;
//		}
//		List<String> infos = new ArrayList<>();
//		infos.add(XConceptExtension.instance().extractName(onEvent));
//		infos.add(String.valueOf(XTimeExtension.instance().extractTimestamp(onEvent)));
//		return infos;
//	}

	public void setSelectedTileTrace(Tiles tiles) {
		this.selectedTileTrace = tiles;
	}

	public Tiles getSelectedTileTrace() {
		return this.selectedTileTrace;
	}

	public void setSelectedTile(Tile e, boolean selected) {
		if (selected) {
			this.selectedTiles.add(e);
		} else {
			this.selectedTiles.remove(e);
		}
		System.out.println("Selected:" + StringUtils.join(selectedTiles, ","));
	}

	public List<Tile> getSelectedTile() {
		return this.selectedTiles;
	}

	public void clearSelectedTiles() {
		for (Tile t : selectedTiles) {
			t.setSelected(false);
		}
		this.selectedTiles.clear();
	}

	public void addPatternAndInstance(ContextPattern<TNode> p,
			PatternInstances<Tile> instances) {
		this.mapContext2PatternInstances.put(p, instances);
		this.mapPattern2Color.put(p, TileDEFAULT.TILE_DEFAULT_PATTERN_COLOR);
	}

	public List<String[]> getPatternStatisticInfo() {
		// TODO:get through controller...
		List<String[]> info = new ArrayList<>();
		for (Entry<ContextPattern<TNode>, PatternInstances<Tile>> entry : mapContext2PatternInstances
				.entrySet()) {
			// Need a list of pattern. 
			ContextPattern<TNode> pattern = entry.getKey();
			List<PatternInstance<Tile>> instances = entry.getValue().getInstances();

			// for each pattern, get statistic info
			String[] record = getRecord(pattern, instances);
			// turn it into a record entry
			// add to csv. 
			info.add(record);
			// export. 
		}
		return info;
	}

	private String[] getRecord(ContextPattern<TNode> pattern,
			List<PatternInstance<Tile>> instances) {
		List<String> values = new ArrayList<>();
		values.add(pattern.getLabel());
		values.add(StringUtils.join(pattern.getPredecessors(), ","));
		values.add(StringUtils.join(pattern.getConcurrences(), ","));
		values.add(StringUtils.join(pattern.getSuccessors(), ","));
		values.add(String.valueOf(instances.size()));
		return values.toArray(new String[values.size()]);
	}

	public ContextPattern<TNode> fitTilesToPattern(List<Tile> selectedTile,
			Tiles selectedTileTrace2) {
		return getFactory().fitTilesToPattern(selectedTile, selectedTileTrace2);
	}

	public PatternInstances<Tile> matchEventToPattern(ContextPattern<TNode> p) {
		PatternInstances<Tile> instances = getFactory().matchEventToPattern(getAllTiles(),
				p);
		addPatternAndInstance(p, instances);
		updatePatternColor(p);
		return instances;
	}

	private void updatePatternColor(ContextPattern<TNode> p) {
		PatternInstances<Tile> instances = getInstancesOfPattern(p);
		Color c = mapPattern2Color.get(p);
		for (PatternInstance<Tile> instance : instances.getInstances()) {
			for (Tile t : instance.getNodes()) {
				t.setLineColor(c);
			}
			instance.getCenter().setCenter(true);
		}
		for (PatternInstance<Tile> antiInstances : instances.getAntiInstances()) {
			antiInstances.getCenter().setAnti(true);
//			antiInstances.getCenter().setLineColor(c);
		}
	}

	public PatternInstances<Tile> getInstancesOfPattern(ContextPattern<TNode> p) {
		return mapContext2PatternInstances.get(p);
	}

	public void setPatternColor(ContextPattern<TNode> p, Color newColor) {
		this.mapPattern2Color.put(p, newColor);
		updatePatternColor(p);

	}

	public void setVisibleAntiInstances(ContextPattern<TNode> p) {
		// TODO Auto-generated method stub
		//Currently, only one
		this.selectedPatterns.clear();
		isShowAnti = true;

		this.selectedPatterns.add(p);
	}

	public void setVisibleInstances(ContextPattern<TNode> p) {
		//Currently, only one
		this.selectedPatterns.clear();
		isShowAnti = false;

		this.selectedPatterns.add(p);
	}

	public PatternFactory<Tiles, Tile> getFactory() {
		return factory;
	}

	public void setFactory(PatternFactory<Tiles, Tile> factory) {
		this.factory = factory;
	}

	public void setclassifier(XEventClassifier classifier) {
		this.setClassifier(classifier);
		getTileLayout().setClassifier(classifier);

		for (int trcIndex = 0; trcIndex < tss.length; trcIndex++) {
			Tiles ts = tss[trcIndex];
			XTrace trc = log.get(trcIndex);

			for (int evtIndex = 0; evtIndex < ts.getTiles().length; evtIndex++) {
				Tile t = ts.getTiles()[evtIndex];

				XEvent e = trc.get(evtIndex);
				t.setLabel(classifier.getClassIdentity(e));

			}
		}
		this.view.updateTileList();
	}

	public void setOnTile(Tile t) {
		this.onTile = t;
	}

	public Tile getOnTile() {
		return this.onTile;
	}

	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}

}
