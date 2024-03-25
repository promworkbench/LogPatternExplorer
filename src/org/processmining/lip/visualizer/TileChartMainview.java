
package org.processmining.lip.visualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.StringUtils;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.framework.util.ui.widgets.ProMTextArea;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.lip.controller.TController;
import org.processmining.lip.controller.TileLayout;
import org.processmining.lip.controller.TileLayoutPOTs;
import org.processmining.lip.controller.TileLayoutStack;
import org.processmining.lip.controller.TileLayoutTimeAbs;
import org.processmining.lip.detection.IPatternDetection;
import org.processmining.lip.detection.PatternDetectionBlockStrucContext;
import org.processmining.lip.detection.PatternDetectionConcurrent;
import org.processmining.lip.detection.PatternDetectionEpisodes;
import org.processmining.lip.detection.PatternDetectionManualSelection;
import org.processmining.lip.detection.PatternDetectionPatternAbstraction;
import org.processmining.lip.detection.PatternDetectionPredecessor;
import org.processmining.lip.detection.PatternDetectionRandom;
import org.processmining.lip.detection.PatternDetectionSuccessors;
import org.processmining.lip.detection.dev.PatternDetectionRepetition;
import org.processmining.lip.detection.dev.PatternDetectionReplacement;
import org.processmining.lip.model.TNode;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.TileDEFAULT;
import org.processmining.lip.model.TileSize;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.model.pattern.ContextPattern;
import org.processmining.lip.model.pattern.PatternInstance;
import org.processmining.lip.model.pattern.PatternInstances;
import org.processmining.lip.model.pattern.PatternToTextFactory;
import org.processmining.lip.model.pattern.metric.PatternInfoField;
import org.processmining.lip.model.pattern.metric.PatternMetricFactory;
import org.processmining.lip.model.pattern.vis.PatternGraphVizView;
import org.processmining.lip.model.pattern.vis.PatternInstanceView;
import org.processmining.lip.visualizer.filter.ColoringPanel;
import org.processmining.lip.visualizer.filter.ColoringValueBasedPanel;
import org.processmining.lip.visualizer.filter.PanelUtil;
import org.processmining.plugins.InductiveMiner.ClassifierChooser;
import org.processmining.plugins.graphviz.dot.Dot2Image;

import com.fluxicon.slickerbox.components.SlickerButton;
import com.fluxicon.slickerbox.components.SlickerSearchField;
import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.google.common.collect.Multiset;

import gnu.trove.map.hash.THashMap;

public class TileChartMainview extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1170613532478743280L;

	private PluginContext context;
	private TController controller;

	private TileSize tileSize = new TileSize();
	private Dimension windowSize;

	private JPanel tileListPane;
	private TileChartView[] listCharts;

	private JComponent controlPanel;
	private JComponent patternView;

	private Map<String, ColoringPanel<?>> mapKeyToColorPanel = new THashMap<>();
	private Map<ContextPattern, PatternInstanceView> mapPatternToView = new THashMap<>();

	public TileChartMainview(XLog log) {
		super(new BorderLayout());

		controller = new TController(this, log);

		//Set up the drawing area.
		tileListPane = new JPanel();
		tileListPane.setBackground(Color.white);
		tileListPane.setLayout(new BoxLayout(tileListPane, BoxLayout.Y_AXIS));

		addTileCharts();

		add(getControllerView(), BorderLayout.WEST);
		//Put the drawing area in a scroll pane.
		JComponent scroller = PanelUtil.configureVHScrollable(tileListPane);

		ProMSplitPane pane = new ProMSplitPane();
		pane.setLeftComponent(scroller);
		pane.setRightComponent(PanelUtil.configureVHScrollable(getPatternView()));
		pane.setDividerLocation(1400);
		add(pane, BorderLayout.CENTER);

		addComponentListener(new ResizeListener());
		addKeyboardListener();
	}

	private JComponent getPatternView() {
		if (patternView != null) {
			return patternView;
		}

		patternView = new JPanel();
//		patternView.setPreferredSize(new Dimension(0,0));
		patternView.setLayout(new BoxLayout(patternView, BoxLayout.Y_AXIS));
		patternView.setBackground(Color.DARK_GRAY);
		patternView.setForeground(Color.WHITE);

		SlickerButton b = new SlickerButton("Create Pattern");
		patternView.add(PanelUtil.packLeftAligned(b));
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				JTextArea ta = new JTextArea(20, 20);

				String s = TEST2;
				ProMTextArea area = new ProMTextArea();
				area.setText(s);

				switch (JOptionPane.showConfirmDialog(null, new JScrollPane(area))) {
				case JOptionPane.OK_OPTION:
					System.out.println(area.getText());
					break;
				}

				ContextPattern<TNode> pattern = PatternToTextFactory
						.convertTextToPattern(area.getText());
				extractPatternInstances(pattern);
			}
		});

		//-----------------------------------
		// Set detection algorithms
		//-----------------------------------
		List<IPatternDetection> detectors = new ArrayList<IPatternDetection>();
		detectors.add(new PatternDetectionManualSelection());
		detectors.add(new PatternDetectionConcurrent());
		detectors.add(new PatternDetectionSuccessors());
		detectors.add(new PatternDetectionPredecessor());
		detectors.add(new PatternDetectionBlockStrucContext());

		detectors.add(new PatternDetectionEpisodes(context));
		detectors.add(new PatternDetectionPatternAbstraction());
		detectors.add(new PatternDetectionRandom());
		
		//TODO Fix...
		detectors.add(new PatternDetectionReplacement());
		detectors.add(new PatternDetectionRepetition());
//		detectors.add(new PatternDetectionConsecutiveLM());
		
		

		final JComboBox<IPatternDetection> detectorCombo = new JComboBox<IPatternDetection>(
				detectors.toArray(new IPatternDetection[detectors.size()]));

//		detectorCombo.setSelectedItem(controller.getTileLayout());
		SlickerDecorator.instance().decorate(detectorCombo);
		detectorCombo.setBackground(Color.DARK_GRAY);
		detectorCombo.setForeground(Color.WHITE);
		detectorCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
		PanelUtil.packLeftAligned(detectorCombo);
		patternView.add(detectorCombo);

		// Run detector button
		SlickerButton bFitPattern = new SlickerButton("Fit Pattern");
		patternView.add(PanelUtil.packLeftAligned(bFitPattern));
		bFitPattern.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {
				IPatternDetection detector = (IPatternDetection) detectorCombo
						.getSelectedItem();

				if (detector.requireUserInput()
						&& controller.getSelectedTile().isEmpty()) {
					return;
				}

				List<ContextPattern<TNode>> ps = detector.detectPatterns(
						controller.getLog(), controller.getFactory(),
						controller.getSelectedTile(), controller.getSelectedTileTrace(),
						controller.getAllTiles());

				for (ContextPattern<TNode> p : ps) {
					extractPatternInstances(p);
				}
				
				// TODO: test significance
				
			}

		});

		// Add Export all patterns button
		SlickerButton bExportAll = new SlickerButton("Export all");
		patternView.add(PanelUtil.packLeftAligned(bExportAll));

		bExportAll.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String separator = " : ";
				String patternBreak = "----------";

				List<ContextPattern> listPatterns = new ArrayList<>(
						mapPatternToView.keySet());

				String parentDir = "./_out/";
				new File(parentDir).mkdirs();
				String filename = parentDir + "Patterns.txt";
				PrintWriter writer = getWriter(filename);

				String filePatternInfo = parentDir + "PatternsInfo.csv";
				PrintWriter writerPatternInfo = getWriter(filePatternInfo);
				writerPatternInfo
						.println(StringUtils.join(PatternInfoField.values(), ";"));
				writer.println("Number of Patterns" + separator + listPatterns.size());
				for (int i = 0; i < listPatterns.size(); i++) {
					ContextPattern p = listPatterns.get(i);
					PatternInstanceView view = mapPatternToView.get(p);

					// Export Pattern as Figure image
					Dot2Image.dot2image(view.getDot(),
							new File(parentDir + "P" + i + ".png"),
							org.processmining.plugins.graphviz.dot.Dot2Image.Type.png);

					// Export Pattern as text. 
					writer.println(PatternToTextFactory.convertPatternToText(p));
					writer.println(patternBreak);
					writer.flush();

					// Export Pattern instances as log.
					PatternInstances<Tile> instances = controller
							.getInstancesOfPattern(p);
					XLog sublog = XFactoryRegistry.instance().currentDefault()
							.createLog(controller.getLog().getAttributes());
					for (PatternInstance instance : instances.getInstances()) {
						if (!sublog.contains(instance.getTrace())) {
							sublog.add(instance.getTrace());
						}
					}
					XesXmlGZIPSerializer serializer = new XesXmlGZIPSerializer();
					String logfilename = parentDir + "P" + i + "_Log.xes.gz";
					exportLog(serializer, sublog, logfilename);

					// Export pattern info
					String[] resPrint = new String[PatternInfoField.values().length];
					resPrint[PatternInfoField.Pnum.ordinal()] = String.valueOf(i);
					resPrint[PatternInfoField.CoreEvt.ordinal()] = String
							.valueOf(p.getLabel());
					resPrint[PatternInfoField.Preds.ordinal()] = StringUtils
							.join(p.getPredecessors(), ",");
					resPrint[PatternInfoField.Concs.ordinal()] = StringUtils
							.join(p.getConcurrences(), ",");
					resPrint[PatternInfoField.Succs.ordinal()] = StringUtils
							.join(p.getSuccessors(), ",");
					for (Entry<PatternInfoField, String> info : instances.getInfo()
							.entrySet()) {
						String value = info.getValue();
						resPrint[info.getKey().ordinal()] = value.contains(" %")
								? value.substring(0, value.length() - 2) : value;
					}

					writerPatternInfo.println(StringUtils.join(resPrint, ";"));
					writerPatternInfo.flush();
				}
				writer.close();
				writerPatternInfo.close();
			}

			private PrintWriter getWriter(String filename) {
				PrintWriter writer = null;
				File file = new File(filename);
				try {
					// if file doesnt exists, then create it
					if (!file.exists()) {
						file.createNewFile();
					}
					writer = new PrintWriter(
							new OutputStreamWriter(new FileOutputStream(file, true)));

				} catch (IOException e) {
					e.printStackTrace();
				}
				return writer;

			}

			private void exportLog(XesXmlGZIPSerializer serializer, XLog clusterLog,
					String logfilename) {
				OutputStream output = null;
				try {
					output = new FileOutputStream(logfilename);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					serializer.serialize(clusterLog, output);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

		return patternView;
	}

	private Component getControllerView() {
		if (controlPanel != null) {
			return controlPanel;
		}

		controlPanel = new JPanel();
		PanelUtil.configPanelForLeftAligned(controlPanel);
		controlPanel.setPreferredSize(new Dimension(300, 300));

		// Add log classifier chooser 
		controlPanel.add(PanelUtil.packLeftAligned(
				PanelUtil.createLabel(controlPanel, "Set classifier: ")));
		final ClassifierChooser classifiers = new ClassifierChooser(controller.getLog());
		classifiers.setMaximumSize(new Dimension(1000, 200));
		Box boxContainer = Box.createHorizontalBox();
		boxContainer.add(classifiers);
		boxContainer.add(Box.createHorizontalGlue());
		boxContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		controlPanel.add(boxContainer);
		classifiers.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				XEventClassifier classifier = classifiers.getSelectedClassifier();
				controller.setclassifier(classifier);

				System.out.println(classifier);
			}
		});

		// Add log classifier chooser 

		boxContainer = Box.createHorizontalBox();
		boxContainer.add(PanelUtil.createLabel(controlPanel, "Show label chars"));
		final JLabel labelchar = PanelUtil.createLabel(controlPanel, String.valueOf(0));
		boxContainer.add(labelchar);
		boxContainer.add(Box.createHorizontalGlue());
		boxContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		controlPanel.add(boxContainer);

		ChangeListener c = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int threshold = source.getValue();
				labelchar.setText(String.valueOf(threshold));
				if (!source.getValueIsAdjusting()) {
					getTileSize().tileWidth = TileSize.DEFAULT_TILE_WIDTH + threshold * 8;
					getTileSize().tileHeight = threshold > 0
							? TileSize.DEFAULT_TILE_WIDTH + 5
							: TileSize.DEFAULT_TILE_WIDTH;
					getTileSize().tileLabelLetter = threshold;

					controller.fireTileLayoutChange();
					updateTileList();
				}
			}
		};
		JSlider labelSlider = PanelUtil.createSlider(controlPanel, c, 0, 6, 0);
		controlPanel.add(labelSlider);

		// Tile chart layout manager
		final JLabel labelVisType = PanelUtil.createLabel(controlPanel,
				"Set layout manager: ");
		controlPanel.add(PanelUtil.packLeftAligned(labelVisType));
		final JPanel layoutSettings = new JPanel();
		PanelUtil.configPanelForLeftAligned(layoutSettings);

		final JComboBox<TileLayout> visCombo = new JComboBox<TileLayout>(
				controller.getAllTileLayouts());

		visCombo.setSelectedItem(controller.getTileLayout());
		SlickerDecorator.instance().decorate(visCombo);
		visCombo.setBackground(Color.DARK_GRAY);
		visCombo.setForeground(Color.WHITE);
		visCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
		visCombo.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				TileLayout layout = (TileLayout) visCombo.getSelectedItem();
				if (!controller.getTileLayout().equals(layout)) {
					controller.setTileLayout(layout);
					layoutSettings.removeAll();
					initLayoutSettings(layoutSettings);
					updateTileList();
					controlPanel.revalidate();
					controlPanel.repaint();
				}
			}
		});
		PanelUtil.packLeftAligned(visCombo);
		controlPanel.add(visCombo);

		// Get Additional Layout settings. 
		initLayoutSettings(layoutSettings);
		controlPanel.add(layoutSettings);

		// Add search bar
		final SlickerSearchField filterSearchField = new SlickerSearchField(220, 23,
				Color.GRAY, Color.DARK_GRAY, Color.WHITE, Color.WHITE);
		filterSearchField.setForeground(Color.WHITE);
		filterSearchField.addSearchListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String filter = filterSearchField.getSearchText().toLowerCase().trim();
//				if (filter.length() > 0) {
//					filter.replaceAll("\\w", "(.*)");
//					filter = "(.*)" + filter + "(.*)";
//				} else {
//					filter = "(.*)";
//				}
				for (Entry<String, ColoringPanel<?>> entry : mapKeyToColorPanel
						.entrySet()) {
					ColoringPanel p = entry.getValue();
					p.setFilter(filter);
				}

			}
		});
		controlPanel.add(PanelUtil.packLeftAlignedWithStrut(filterSearchField));

		// Add similar value similar color slider
		String similarDescription = "Select similarity value: ";
		final JLabel similarLabel = PanelUtil.createLabel(controlPanel,
				similarDescription);
		boxContainer = Box.createHorizontalBox();
		boxContainer.add(similarLabel);
		final JLabel similarValueLabel = PanelUtil.createLabel(controlPanel,
				String.valueOf(0));
		boxContainer.add(similarValueLabel);
		boxContainer.add(Box.createHorizontalGlue());
		boxContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		controlPanel.add(boxContainer);

		c = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int threshold = source.getValue();
				similarValueLabel.setText(String.valueOf(threshold));
				if (!source.getValueIsAdjusting()) {

					controller.setColorSimilarThreshold(threshold);
					updateTileList();
				}
			}
		};
		JSlider similarSlider = PanelUtil.createSlider(controlPanel, c, 0, 1000, 0);
		controlPanel.add(similarSlider);

		// Add coloring panels. 	 
		Map<String, Multiset<String>> mapKey2value = controller.getMapKeyToValue();

		JPanel mainColoringPanel = new JPanel();
		PanelUtil.configPanelForLeftAligned(mainColoringPanel);
		JComponent comp = PanelUtil.configureAnyScrollable(mainColoringPanel);
		for (Entry<String, Multiset<String>> entry : mapKey2value.entrySet()) {
			if (entry.getKey().equals(XTimeExtension.KEY_TIMESTAMP)) {
				continue;
			}
			ColoringPanel<String> colorPanel = new ColoringValueBasedPanel(entry.getKey(),
					entry.getValue(), controller);
			mapKeyToColorPanel.put(entry.getKey(), colorPanel);
			mainColoringPanel.add(PanelUtil.packLeftAligned(colorPanel.getPanel()));
		}

		controlPanel.add(PanelUtil.packLeftAligned(comp));

		// Add parser panel. 
//		AttrParser p = new AttrParser(XConceptExtension.KEY_NAME, "[A][_]");
//		ColoringPanel<AttrParser> colorPatternPanel = new ColoringParserBasedPanel(XConceptExtension.KEY_NAME,
//				controller.getMapKeyToValue().keySet(), Arrays.asList(new AttrParser[] { p }), controller);
//		controlPanel.add(PanelUtil.packLeftAligned(colorPatternPanel.getPanel()));

		SlickerButton b = new SlickerButton("Reset colors");
		controlPanel.add(PanelUtil.packLeftAligned(b));

		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.resetColorMap();
				for (ColoringPanel<?> colorPanel : mapKeyToColorPanel.values()) {
					colorPanel.getColorMap().clear();
					colorPanel.getPanel().repaint();
				}
			}
		});

		return controlPanel;
	}

	private void initLayoutSettings(JPanel layoutSettings) {
		if (controller.getTileLayout() instanceof TileLayoutStack) {
			getTimeDurConcurSettings(layoutSettings);
		} else if (controller.getTileLayout() instanceof TileLayoutTimeAbs) {
			getAbsTimeSettings(layoutSettings);
		}
	}

	private void getAbsTimeSettings(JPanel layoutSettings) {
		final String description = "Relative Time max size: ";
		final JLabel descriptionLabel = PanelUtil.createLabel(controlPanel, description);
		Box boxContainer = Box.createHorizontalBox();
		boxContainer.add(descriptionLabel);
		final JLabel label2 = PanelUtil.createLabel(controlPanel, String.valueOf(500));
		boxContainer.add(label2);
		boxContainer.add(Box.createHorizontalGlue());
		boxContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		layoutSettings.add(boxContainer);

		ChangeListener c = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int threshold = source.getValue();
				label2.setText(String.valueOf(threshold));
				if (!source.getValueIsAdjusting()) {
					// ApplyThreshold
					//						controller.setMaxCostOfEdge(threshold);
					updateValue(threshold);
				}
			}

		};
		JSlider slider = PanelUtil.createSlider(controlPanel, c, 300, 5000, 500);
		layoutSettings.add(slider);
		slider.addMouseListener(new DoubleClickMouseListen(slider));
		layoutSettings.setAlignmentX(Component.LEFT_ALIGNMENT);

	}

	private void getTimeDurConcurSettings(JPanel layoutSettings) {
		final String description = "Timedur. for concurrence (sec.): ";
		final JLabel descriptionLabel = PanelUtil.createLabel(controlPanel, description);
		Box boxContainer = Box.createHorizontalBox();
		boxContainer.add(descriptionLabel);
		final JLabel label2 = PanelUtil.createLabel(controlPanel, String.valueOf(1));
		boxContainer.add(label2);
		boxContainer.add(Box.createHorizontalGlue());
		boxContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		layoutSettings.add(boxContainer);

		ChangeListener c = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int threshold = source.getValue();
				label2.setText(String.valueOf(threshold));
				if (!source.getValueIsAdjusting()) {
					updateValue(threshold);
				}
			}
		};
		JSlider slider = PanelUtil.createSlider(controlPanel, c, 0, Integer.MAX_VALUE, 1);
		slider.addMouseListener(new DoubleClickMouseListen(slider));
		layoutSettings.add(slider);
		layoutSettings.setAlignmentX(Component.LEFT_ALIGNMENT);
	}

	private void updateValue(int threshold) {
		if (controller.getTileLayout() instanceof TileLayoutTimeAbs) {
			((TileLayoutTimeAbs) controller.getTileLayout()).setWindowWidth(threshold);
			controller.fireTileLayoutChange();
			updateTileList();
		} else if (controller.getTileLayout() instanceof TileLayoutStack) {
			((TileLayoutStack) controller.getTileLayout()).setTimeDur(threshold);
			controller.fireTileLayoutChange();
			updateTileList();
		}
	}

	class DoubleClickMouseListen extends MouseAdapter {

		private JSlider slider;

		public DoubleClickMouseListen(JSlider slider) {
			this.slider = slider;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			//TODO: Check the values within the min and max of slider.
			//TODO: check it is a number. 
			if (e.getClickCount() == 2) {
				ProMTextField area = new ProMTextField();
				area.setText("");

				switch (JOptionPane.showConfirmDialog(null, new JScrollPane(area),
						"Set time window for concurrency:",
						JOptionPane.INFORMATION_MESSAGE)) {
				case JOptionPane.OK_OPTION:
					String s = area.getText();
					int d = Integer.parseInt(s);
					slider.setValue(d);
					break;
				}
			}
		}
	}

	public void removePattern(ContextPattern<TNode> p) {
		PatternInstances<Tile> instances = controller.getInstancesOfPattern(p);
		for (PatternInstance<Tile> instance : instances.getInstances()) {
			instance.getCenter().setSelected(false);
			instance.getCenter().setColor(TileDEFAULT.TILE_DEFAULT_FILL_COLOR);
			instance.getCenter().setCenter(false);
			for (Tile t : instance.getNodes()) {
				t.setSelected(false);
				t.setColor(TileDEFAULT.TILE_DEFAULT_FILL_COLOR);
				t.setLineColor(null);
			}
		}
		for (PatternInstance<Tile> instance : instances.getAntiInstances()) {
			instance.getCenter().setSelected(false);
			instance.getCenter().setAnti(false);
			instance.getCenter().setColor(TileDEFAULT.TILE_DEFAULT_FILL_COLOR);

		}
		controller.clearSelectedTiles();
		patternView.remove(mapPatternToView.get(p));
		patternView.revalidate();
		patternView.repaint();
		this.repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		drawInfo(g);

	}

	private void drawInfo(Graphics g) {
		Color backupColour = g.getColor();
		Font backupFont = g.getFont();

		Point point = this.getMousePosition();
		if (point == null) {
			return;
		}

		int x = (int) (point.getX() + 20);
		int y = (int) (point.getY() + 20);

		int w = x + 5;
		int h = y + 20;
		int wText = 300;
		int hText = 0;
		//		g.setColor(Color.WHITE);

		// Painting the info of event where the mouse is.
		if (controller.getOnEvent() != null) {
			XAttributeMap entrySet = controller.getOnEvent().getAttributes();
			drawInfoPanel(g, x, y, w, wText, hText, entrySet);
		} else if (controller.getOnTrace() != null) {
			XAttributeMap entrySet = controller.getOnTrace().getAttributes();
			drawInfoPanel(g, x, y, w, wText, hText, entrySet);
		}
		g.setColor(backupColour);
		g.setFont(backupFont);
	}

	private void drawInfoPanel(Graphics g, int x, int y, int w, int wText, int hText,
			XAttributeMap entrySet) {
		int h;
		Font font = g.getFont();
		FontRenderContext frc = ((Graphics2D) g).getFontRenderContext();

		for (Entry<String, XAttribute> info : entrySet.entrySet()) {

			Rectangle2D boundsTemp = font.getStringBounds(
					info.getKey() + " : " + info.getValue().toString(), frc);
			wText = wText < (int) boundsTemp.getWidth() + 10
					? (int) boundsTemp.getWidth() + 10 : wText;
			hText += (int) boundsTemp.getHeight() + 5;
		}

		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(x, y, wText, hText + 10 < 100 + 10 ? 100 + 10 : hText + 10);

		h = y + 20;
		g.setColor(Color.WHITE);
		for (Entry<String, XAttribute> info : entrySet.entrySet()) {
			g.drawString(info.getKey() + " : " + info.getValue().toString(), w, h);

			Rectangle2D boundsTemp = font.getStringBounds(info.getKey(), frc);
			h += (int) boundsTemp.getHeight() + 5;
		}
	}

	private void addTileCharts() {
		List<Tiles> tilesList = controller.getListOfTilesVisible();

		if (listCharts == null) {
			listCharts = new TileChartView[tilesList.size()];
		}
		tileListPane.removeAll();

		for (int i = 0; i < tilesList.size(); i++) {

//			Tiles tiles = controller.getTiles(i);
			Tiles tiles = tilesList.get(i);

			TileChartView chart = listCharts[i];
			if (chart == null || controller.getTileLayout() instanceof TileLayoutPOTs) {
				chart = new TileChartView(tiles, controller);
//				chart.setC(controller);

				listCharts[i] = chart;
				if (i % 2 == 0) {
					chart.setBackground(Color.WHITE);
				} else {
					chart.setBackground(new Color(244, 244, 244));
				}

			} else {
				chart.removeAll();
				chart.setTiles(tiles);
				chart.revalidate();
				chart.repaint();
			}//			

			tileListPane.add(chart);

		}
	}

	public void updateTileList() {
		addTileCharts();

		tileListPane.revalidate();
		tileListPane.repaint();
	}

	String TEST = "center:\n" + "(72, O_ACCEPTED) \n" + "nodes:\n" + "(73, A_APPROVED);\n"
			+ "(75, A_ACTIVATED);\n" + "(74, A_REGISTERED);\n" + "(72, O_ACCEPTED);\n"
			+ "edges:\n";

	String TEST2 = "center:\n" + "(72, O_ACCEPTED)\n" + "nodes:\n"
			+ "(76, W_Valideren aanvraag);\n" + "(72, O_ACCEPTED);\n" + "edges:\n"
			+ "(72 -> 76, DF);\n" + "(72 -> 76, EF);\n";

	public void extractPatternInstances(ContextPattern<TNode> p) {
		// Add export pattern info button 
//		addExportPatternCSVButton();

		MyRunnable r = new MyRunnable(p, this);
		r.run();
//		SwingUtilities.invokeLater(r);

	}

	class MyRunnable implements Runnable {

		private ContextPattern<TNode> p;
		private TileChartMainview view;

		public MyRunnable(ContextPattern<TNode> p, TileChartMainview tileChartMainview) {
			this.p = p;
			this.view = tileChartMainview;
		}

		public void run() {
			// Visualize snapshots of pattern
			PatternInstances<Tile> instances = controller.matchEventToPattern(p);
			Map<PatternInfoField, String> mapRes = PatternMetricFactory.computeMetricsMap(
					controller.getLog(), p, instances, controller.getMapKeyToValue(), controller.getClassifier());

			instances.setInfo(mapRes);
			List<String> res = new ArrayList<String>();
			for (Entry<PatternInfoField, String> info : mapRes.entrySet()) {
				res.add(info.getKey() + " : " + info.getValue());
			}
			PatternInstanceView pview = new PatternGraphVizView(view, p, instances, res,
					controller);
			mapPatternToView.put(p, pview);

			pview.setAlignmentX(Component.LEFT_ALIGNMENT);

			pview.setContext(context);

			patternView.add(pview);
			patternView.revalidate();
			patternView.repaint();

			updateTileList();
			controller.clearSelectedTiles();

		}

	}

//	private void addExportPatternCSVButton() {
//		if (patternView.getComponentCount() > 0) {
//			return;
//		}
//		SlickerButton buttonPatternInfo = new SlickerButton("Export Pattern Info");
//		patternView.add(PanelUtil.packLeftAligned(buttonPatternInfo));
//		buttonPatternInfo.addActionListener(new ActionListener() {
//
//			public void actionPerformed(ActionEvent e) {
//
//				CSVWriter writer = null;
//				JFileChooser chooser = new JFileChooser();
//				chooser.setAcceptAllFileFilterUsed(true);
//
//				if (chooser.showSaveDialog(chooser) != JFileChooser.APPROVE_OPTION) {
//					return;
//				}
//				File f = chooser.getSelectedFile();
//				String file_name = f.toString();
//				if (!(file_name.endsWith(".csv"))) {
//					file_name += ".csv";
//				}
//				try {
//					writer = new CSVWriter(new FileWriter(f), ';');
//
//					List<String[]> info = controller.getPatternStatisticInfo();
//
//					for (int i = 0; i < info.size(); i++) {
//						writer.writeNext(info.get(i));
//					}
//
//					writer.close();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//
//			}
//		});
//	}

	private void addKeyboardListener() {
		this.setFocusable(true);

		// add "ctrl + plus" is scale up. 
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_MASK),
				"ScaleUpAction");
		getActionMap().put("ScaleUpAction", new ScaleUpAction());

		// add "ctrl + minus" is scale down. 
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK),
				"ScaleDownAction");
		getActionMap().put("ScaleDownAction", new ScaleDownAction());
	}

	public class ScaleUpAction extends AbstractAction {
		private static final long serialVersionUID = -7030421619101737953L;

		public void actionPerformed(ActionEvent e) {
			System.out.println("Scale up");
			tileSize.tileWidth += 2;
			tileSize.tileHeight += 2;
			controller.fireTileLayoutChange();
			updateTileList();
		}
	}

	public class ScaleDownAction extends AbstractAction {
		private static final long serialVersionUID = -8031201301917482621L;

		public void actionPerformed(ActionEvent e) {
			System.out.println("Scale down");
			tileSize.tileWidth -= 2;
			tileSize.tileHeight -= 2;
			controller.fireTileLayoutChange();
			updateTileList();
		}
	}

	public class ResizeListener implements ComponentListener {

		public void componentResized(ComponentEvent e) {
			System.out.println(e.getComponent().getSize());
			//			controller.setTileWindowSize(e.getComponent().getSize());
		}

		public void componentHidden(ComponentEvent e) {
		}

		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentShown(ComponentEvent cEvt) {
			//	    	super.componentShown(cEvt);
			Component src = (Component) cEvt.getSource();
			//	        src.requestFocusInWindow();
			// XX about Java: apparently, when component is shown, this function is not called but componentResized is called.
			System.out.println("Size is " + src.getSize());
			windowSize = src.getSize();
		}
	}

	public TileSize getTileSize() {
		return this.tileSize;
	}

	public void setContext(PluginContext context) {
		this.context = context;
	}

}
