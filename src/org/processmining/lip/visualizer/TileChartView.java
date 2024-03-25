package org.processmining.lip.visualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JPanel;

import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.lip.controller.TController;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.Tiles;
import org.processmining.lip.model.tilepo.TilesPO;

public class TileChartView extends JPanel {

	private Tiles tiles;
	private TController c;

	public TileChartView(Tiles tiles, TController c) {
		super();

		this.tiles = tiles;
		this.c = c;

		if (tiles instanceof TilesPO) {
			setBackground(WidgetColors.COLOR_ENCLOSURE_BG);
			setLayout(new BorderLayout());
			setMinimumSize(new Dimension(200, 100));
//			setMaximumSize(new Dimension(1000, 1000));
//			setPreferredSize(new Dimension(1000, 600));
			add(Box.createHorizontalStrut(5), BorderLayout.WEST);
			add(Box.createHorizontalStrut(5), BorderLayout.EAST);
			TileChartPOGraph panel = ((TilesPO) tiles).getPanel();
			((TilesPO) tiles).getPanel().setController(c);
			this.add(panel, BorderLayout.CENTER);
			for (MouseListener lc : panel.getComp().getMouseListeners()) {
				if (lc instanceof TileMouseListener) {
					panel.getComp().removeMouseListener(lc);
				}
			}
			TileMouseListener l = new TilePOMouseListener(tiles, c, panel.getComp());
			panel.getComp().addMouseMotionListener(l);
			panel.getComp().addMouseListener(l);

		} else {
			TileMouseListener l = new TileMouseListener(tiles, c);
			this.addMouseMotionListener(l);
			this.addMouseListener(l);
		}

		this.setBackground(Color.LIGHT_GRAY);
		setPreferredSize(tiles.getDimension());

//		this.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

	}

//	public TileChart(XTrace t, XTimeBounds logTimeBoundaries) {
//			this(t);
//			this.timebounds = logTimeBoundaries;
//		}

	public void setTiles(Tiles tiles) {
		this.tiles = tiles;
		setPreferredSize(tiles.getDimension());

		if (!(tiles instanceof TilesPO)) {
			for (MouseListener lc : getMouseListeners()) {
				if (lc instanceof TileMouseListener || lc instanceof TilePOMouseListener) {
					removeMouseListener(lc);
				}
			}
			TileMouseListener l = new TileMouseListener(tiles, c);
			this.addMouseMotionListener(l);
			this.addMouseListener(l);
		}
	}

	public TController getC() {
		return c;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (tiles instanceof TilesPO) {
			// don't draw
		} else {
			for (Tile t : tiles.getTiles()) {
				t.paintComponent(g);
			}
		}
	}

}
