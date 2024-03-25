package org.processmining.lip.visualizer.filter;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

public class PanelUtil {

//	@SuppressWarnings("rawtypes")
//	public static JComponent configureMultiSelectionList(JList list, String title,
//			String description) {
//		cofigureListBasic(list);
//		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		list.setSelectionInterval(0, list.getModel().getSize() - 1);
//		return configureAnyScrollable(list);
//	}

//	protected static Color colorListBgSelected = Color.LIGHT_GRAY;

	@SuppressWarnings("rawtypes")
	public static void cofigureListBasic(JList list) {
		list.setFont(list.getFont().deriveFont(13f));
		list.setBackground(Color.GRAY);
		list.setForeground(Color.WHITE);
		list.setFont(list.getFont().deriveFont(12f));
	}

	public static JComponent configureVHScrollable(JComponent scrollable) {
		JScrollPane listScrollPane = new JScrollPane(scrollable);
		listScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		listScrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
		listScrollPane.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		JScrollBar vBar = listScrollPane.getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, Color.WHITE, new Color(30, 30, 30),
				new Color(80, 80, 80), 4, 12));
		JScrollBar hBar = listScrollPane.getHorizontalScrollBar();
		hBar.setUI(new SlickerScrollBarUI(hBar, Color.WHITE, new Color(30, 30, 30),
				new Color(80, 80, 80), 4, 12));
		listScrollPane.setBackground(Color.WHITE);

		return listScrollPane;
	}

	public static JComponent configureAnyScrollable(JComponent scrollable) {
		JScrollPane listScrollPane = new JScrollPane(scrollable);
		listScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		listScrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
		listScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 8, 10, 10));
		JScrollBar vBar = listScrollPane.getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, Color.GRAY, new Color(30, 30, 30),
				new Color(80, 80, 80), 4, 12));
		listScrollPane.setBackground(Color.DARK_GRAY);

		return listScrollPane;
	}

//	public class SelectedListCellRenderer extends DefaultListCellRenderer {
//		private static final long serialVersionUID = -2089347684127611653L;
//
//		@SuppressWarnings("rawtypes")
//		@Override
//		public Component getListCellRendererComponent(JList list, Object value, int index,
//				boolean isSelected, boolean cellHasFocus) {
//			Component c = super.getListCellRendererComponent(list, value, index,
//					isSelected, cellHasFocus);
//
////			c.setForeground(Color.YELLOW);
//			if (isSelected) {
//				c.setBackground(Color.DARK_GRAY.brighter());
//				c.setForeground(Color.WHITE);
//			}
//			return c;
//		}
//	}
	
	public static void configPanelForLeftAligned(JComponent component){
		component.setLayout(new BoxLayout(component, BoxLayout.Y_AXIS));
		component.setBackground(Color.DARK_GRAY);
		component.setForeground(Color.WHITE);
	}

	public static JComponent packLeftAligned(JComponent component) {
		Box boxContainer = Box.createHorizontalBox();
		boxContainer.add(component);
		boxContainer.add(Box.createHorizontalGlue());
		boxContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		return boxContainer;
	}

	public static JComponent packLeftAlignedWithStrut(JComponent component) {

		Box boxContainer = Box.createHorizontalBox();
//		boxContainer.add(Box.createHorizontalGlue());
		boxContainer.add(Box.createHorizontalStrut(10));
		boxContainer.add(component);
		boxContainer.add(Box.createHorizontalStrut(10));
		boxContainer.add(Box.createHorizontalGlue());
		boxContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		return boxContainer;
	}
	
	public static JLabel createLabel(JComponent panel, final String textlabel, Border border ) {
		final JLabel labelUnfoldingThreshold = new JLabel(textlabel);
		SlickerDecorator.instance().decorate(labelUnfoldingThreshold);
		labelUnfoldingThreshold.setForeground(panel.getForeground());
		labelUnfoldingThreshold.setBorder(border);
		labelUnfoldingThreshold.setAlignmentX(Component.LEFT_ALIGNMENT);
		return labelUnfoldingThreshold;
	}

	public static JLabel createLabel(JComponent panel, final String textlabel) {
		return createLabel(panel, textlabel, paddingBorder);
	}

	public static JLabel createHelpLabel(JComponent panel, final String textlabel) {
		final JLabel labelUnfoldingThreshold = new JLabel(textlabel);
		SlickerDecorator.instance().decorate(labelUnfoldingThreshold);
		labelUnfoldingThreshold.setForeground(Color.GRAY);
		labelUnfoldingThreshold.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		labelUnfoldingThreshold.setAlignmentX(Component.LEFT_ALIGNMENT);
		return labelUnfoldingThreshold;
	}

	private static Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
	private static Border paddingWidthBorder = BorderFactory.createEmptyBorder();

	public static JSlider createSlider(JComponent panel, ChangeListener c, int min,
			int max, int init) {
		JSlider slider = new JSlider(min, max, init);
		slider.addChangeListener(c);
//		SlickerDecorator.instance().decorate(sliderUnfoldingThreshold);
		slider.setForeground(panel.getForeground());
		slider.setBackground(panel.getBackground());
		slider.setBorder(paddingBorder);
		slider.setAlignmentX(Component.LEFT_ALIGNMENT);
		slider.setBorder(paddingBorder);
		return slider;
	}

}
