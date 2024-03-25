package org.processmining.lip.visualizer.filter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.processmining.lip.controller.TController;

import com.fluxicon.slickerbox.components.SlickerButton;
import com.google.common.collect.Multiset;

public abstract class ColoringPanel<T> {

	private String title;
	private Multiset<T> values;

	public Multiset<T> getValues() {
		return values;
	}

	public void setValues(Multiset<T> values) {
		this.values = values;
	}

	private Map<T, Color> map = new HashMap<>();

	private JPanel panel;
	private JList<T> list;
	DefaultListModel<T> model;
	private TController controller;

	public ColoringPanel(String key, Multiset<T> values) {
		this.title = key;
		this.values = values;
		model = new DefaultListModel<T>();
		for (T t : values.elementSet()) {
			model.addElement(t);
		}
		list = new JList(model);
		createPanel();
	}

	public JList<T> getList() {
		return list;
	}

	public abstract void setFilter(String filter);

	public void setList(JList<T> list) {
		this.list = list;
	}

	public Color getColor(T object) {
		Color c = map.get(object);
		return c;
	}

	public JPanel getPanel() {
		if (panel == null) {
			createPanel();
		}
		return panel;
	}

	protected void createPanel() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.DARK_GRAY);
		panel.setForeground(Color.WHITE);

		list.getSelectionModel()
				.addListSelectionListener(new DefaultListSelectionListener());
//			JComponent comp = PProjectionConfigPanel.configureSingleSelectionList(list,
//					"Select " + title, "Visualize ");
		PanelUtil.cofigureListBasic(list);

		JComponent comp = PanelUtil.configureAnyScrollable(list);
//		JComponent comp = PanelUtil.configureVHScrollable(list);

		list.setCellRenderer(new SelectedListCellRenderer());

		JLabel label = PanelUtil.createLabel(panel, "Select color for " + title);

		panel.add(label, BorderLayout.NORTH);
		panel.add(comp, BorderLayout.CENTER);

		// Add functions... 
		JPanel panelButtons = new JPanel();
		panelButtons.setForeground(Color.WHITE);
		panelButtons.setBackground(Color.DARK_GRAY);
//		panelButtons.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		SlickerButton bAutoColor = new SlickerButton("Auto Color");
//		add(PanelUtil.packLeftAligned(bExport));
		panelButtons.add(bAutoColor);
		bAutoColor.addActionListener(getAutoColorActionListener());

		SlickerButton buttonSortFreq = new SlickerButton("Sort Freq.");
//		add(PanelUtil.packLeftAligned(buttonColorPattern));
		panelButtons.add(buttonSortFreq);
		buttonSortFreq.addActionListener(getSortFreqActionListener());
		SlickerButton buttonSortA = new SlickerButton("Sort Alphab.");
//		add(PanelUtil.packLeftAligned(buttonColorPattern));
		panelButtons.add(buttonSortA);
		buttonSortA.addActionListener(getSortAlphabActionListener());

		panel.add(PanelUtil.packLeftAligned(panelButtons), BorderLayout.SOUTH);
	}

	protected abstract ActionListener getSortAlphabActionListener();

	protected abstract ActionListener getSortFreqActionListener();

	protected abstract ActionListener getAutoColorActionListener();
	
	public class DefaultListSelectionListener implements ListSelectionListener {

		@SuppressWarnings("unchecked")
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				if (list.getSelectedIndex() == -1) {
					//No selection, disable fire button.
					//fireButton.setEnabled(false);
				} else {
					Color newColor = JColorChooser.showDialog(panel,
							"Choose Background Color", null);
					if (newColor != null) {
						map.put((T) list.getSelectedValue(), newColor);
					} else {
						map.remove((T) list.getSelectedValue());
					}
//				controller.update();
					list.setSelectedIndices(new int[0]);
				}
			}
		}
	}

	public class SelectedListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = -2089347684127611653L;

		@SuppressWarnings("rawtypes")
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);

			c.setForeground(map.get(value));
			return c;
		}
	}

	public Map<T, Color> getColorMap() {
		return this.map;
	}

	public String getTitle() {
		return this.title;
	}

	public TController getController() {
		return controller;
	}

	public void setController(TController controller) {
		this.controller = controller;
	}

}
