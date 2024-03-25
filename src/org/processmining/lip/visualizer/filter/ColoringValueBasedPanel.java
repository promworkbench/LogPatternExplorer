package org.processmining.lip.visualizer.filter;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListSelectionEvent;

import org.processmining.lip.controller.TController;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

public class ColoringValueBasedPanel extends ColoringPanel<String> {

	public ColoringValueBasedPanel(String key, Multiset<String> values, TController c) {
		super(key, values);
		setController(c);
		getList().getSelectionModel()
				.addListSelectionListener(new TileListSelectionListener(c));
	}

	public class TileListSelectionListener extends DefaultListSelectionListener {
		TController c;

		public TileListSelectionListener(TController c) {
			this.c = c;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
//			super.valueChanged(e);
			c.setColorMap(getKey(), getColorMap());
//			c.updateTileColor();
		}
	}

	public String getKey() {
		return getTitle();
	}

//	public void setFilter(String filter) {
//		DefaultListModel<String> model = (DefaultListModel<String>) getList().getModel();
//		model.clear();
//		for (String s : getValues()) {
//			if (s.toLowerCase().matches(filter)) {
//				model.addElement(s);
//			}
//		}
//	}

	public void setFilter(String filter) {
		List<String> matchedValues = new ArrayList<>();
		for (String s : getValues().elementSet()) {
			if (s.toLowerCase().contains(filter)) {
				matchedValues.add(s);
			}
		}
		getList().setListData(matchedValues.toArray(new String[matchedValues.size()]));
	}

	protected ActionListener getAutoColorActionListener() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {

//				Color[] colors = ColorScheme.COLOR_BREWER_12CLASS_PAIRED.getColors();
//				Color[] colors = ColorScheme.COLOR_BREWER_9CLASS_SET21.getColors();
//				Color[] colors = new Color[] {  new Color(55, 126, 184), new Color(77, 175, 74), new Color(177,89,40),
//						new Color(152, 78, 163), new Color(255, 127, 0), new Color(255, 255, 51), new Color(166, 86, 40),
//						new Color(247, 129, 191), new Color(153, 153, 153) , Color.LIGHT_GRAY}; 
//				
				Color[] colors = new Color[] { Color.DARK_GRAY, new Color(55, 126, 184),
						new Color(77, 175, 74), new Color(152, 78, 163),
						new Color(255, 127, 0), new Color(255, 255, 51),
						new Color(166, 86, 40), new Color(247, 129, 191),
						new Color(153, 153, 153), Color.LIGHT_GRAY };
//				
//				Color[] colors =  new Color[]{
//				new Color(166,206,227),
//				new Color(31,120,180),
//				new Color(178,223,138),
//				new Color(51,160,44),
//				new Color(251,154,153),
////				new Color(227,26,28),
//				new Color(253,191,111),
//				new Color(255,127,0),
//				new Color(202,178,214),
//				new Color(106,61,154),
////				new Color(255,255,153),
////				new Color(177,89,40),  
//				Color.LIGHT_GRAY};
				int i = 0;
				for (String type : Multisets.copyHighestCountFirst(getValues())
						.elementSet()) {
					getColorMap().put(type, colors[i++]);

					i = i < colors.length ? i : colors.length - 1;

				}
				getController().setColorMap(getKey(), getColorMap());
				getPanel().repaint();
			}
		};
	}

	protected ActionListener getSortFreqActionListener() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				List<String> matchedValues = new ArrayList<>();
				for (String type : Multisets.copyHighestCountFirst(getValues())
						.elementSet()) {
					matchedValues.add(type);
				}
				getList().setListData(
						matchedValues.toArray(new String[matchedValues.size()]));
			}

		};
	}

	protected ActionListener getSortAlphabActionListener() {
		return new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				List<String> matchedValues = new ArrayList<>(getValues().elementSet());
				Collections.sort(matchedValues);

				getList().setListData(
						matchedValues.toArray(new String[matchedValues.size()]));
			}

		};
	}

}
