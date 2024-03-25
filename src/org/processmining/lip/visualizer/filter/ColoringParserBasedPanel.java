package org.processmining.lip.visualizer.filter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;

import org.processmining.lip.controller.TController;

import com.fluxicon.slickerbox.components.SlickerButton;
import com.google.common.collect.Multiset;

public class ColoringParserBasedPanel extends ColoringPanel<AttrParser> {

//	String key = XConceptExtension.instance().KEY_NAME;
//	Pattern p = Pattern.compile("[A][_]");
//	
//	Color c = Color.red;

	public ColoringParserBasedPanel(String key, final Collection<String> keys,
			Multiset<AttrParser> parsers, TController c) {
		super(key, parsers);

		// TODO Auto-generated constructor stub
		SlickerButton b = new SlickerButton("Add Parser");
		getPanel().add(b, BorderLayout.SOUTH);

		b.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {

				JLabel label = new JLabel("Select attribute key:");
				JComboBox<Object> keyCombo = new JComboBox<Object>(keys.toArray());

				JLabel label2 = new JLabel("Type Regex parser:");
				keyCombo.setSelectedIndex(0);
				JTextField parserField = new JTextField();

				final JComponent[] inputs = new JComponent[] { label, keyCombo, label2,
						parserField };
				String[] options = new String[] { "Add parser", "Cancel" };

				int res = JOptionPane.showOptionDialog(null, inputs, "Add Parser Dialog",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						options, options[0]);

				if (res == JOptionPane.OK_OPTION && parserField.getText() != null
						&& !parserField.getText().isEmpty()) {
					@SuppressWarnings("rawtypes")
					DefaultListModel listmodel = (DefaultListModel) getList().getModel();

					listmodel.addElement(
							new AttrParser(keyCombo.getSelectedItem().toString(),
									parserField.getText()));

				}
			}
		});
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
			c.setParserColorMap(getKey(), getColorMap());
//			c.updateTileColor();
		}
	}

	public String getKey() {
		return this.getTitle();
	}

	public void setFilter(String filter) {
		// TODO Auto-generated method stub
		
	}

	protected ActionListener getAutoColorActionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	protected ActionListener getSortFreqActionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	protected ActionListener getSortAlphabActionListener() {
		// TODO Auto-generated method stub
		return null;
	}



}
