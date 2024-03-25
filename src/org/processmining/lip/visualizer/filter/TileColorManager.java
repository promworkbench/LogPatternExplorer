package org.processmining.lip.visualizer.filter;

import java.awt.Color;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.processmining.framework.util.ui.widgets.ColorScheme;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

import gnu.trove.map.hash.THashMap;

public class TileColorManager {
	
	
	private Map<String, Map<String, Color>> mapAttrKey2AttrValue2Color = new THashMap<>();
	
	private Map<String, Map<String, Color>> mapAttrKey2Pattern = new THashMap<>();
	
	
	public static Map<XEventClass, Color> createColorMap(XEventClasses eventClasses) {
		Ordering<XEventClass> sizeOrder = new Ordering<XEventClass>() {

			public int compare(XEventClass o1, XEventClass o2) {
				return Ints.compare(o1.size(), o2.size());
			}

		}.reverse().compound(Ordering.natural());
		ImmutableList<XEventClass> listSet = sizeOrder.immutableSortedCopy(eventClasses.getClasses());
		Map<XEventClass, Color> colorMap = Maps.newHashMap();
		int i = 0;
		for (XEventClass eClass : listSet) {
			colorMap.put(eClass, ColorScheme.COLOR_BREWER_12CLASS_PAIRED.getColor(i++));
		}
		return colorMap;
	}
	
}
