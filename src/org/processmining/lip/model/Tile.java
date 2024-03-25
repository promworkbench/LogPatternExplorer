package org.processmining.lip.model;

import java.awt.Color;
import java.awt.Graphics;

public interface Tile extends TNode {

	void setSelected(boolean isSelected);

	void setColor(Color tILE_DEFAULT_FILL_COLOR);

	void setCenter(boolean setCenter);

	void setLineColor(Color linecolor);

	void setAnti(boolean b);

	void paintComponent(Graphics g);

	boolean isSelected();

	void setLabel(String classIdentity);

	void setLabelLetters(int tileLabelLetter);

	void setID(int i);

	

//	private int x;
//
//	private int y;
//
//	private int width;
//	private int height;
//
//	private Color fillcolor = TileDEFAULT.TILE_DEFAULT_FILL_COLOR;
//	private Color linecolor = null;
//
//	private boolean isSelected = false;
//	private boolean isCenter = false;
//	private boolean isAnti = false;
//
//	private int labelLetters = 0;
//
//	protected Tile(int newX, int j) {
//		this.x = newX;
//		this.y = j;
//	}
//
//	public Tile(int x, int y, int width, int height) {
//		this(x, y);
//		this.width = width;
//		this.height = height;
//	}
//
//	public void paintComponent(Graphics g) {
//		Color orgColor = g.getColor();
//		if (fillcolor == null) {
//			g.setColor(TileDEFAULT.TILE_DEFAULT_FILL_COLOR);
//		} else {
//			g.setColor(fillcolor);
//		}
//		g.fillRect(x, y, width, height);
//
//		if (labelLetters > 0 || labelLetters == -1) {
//			String l = labelLetters == -1 || labelLetters > label.length() ? label
//					: label.substring(0, labelLetters);
//
//			Font font = g.getFont();
//			Font newFont = font.deriveFont(3);
//			FontMetrics metrics = g.getFontMetrics(newFont);
//			int hgt = metrics.getHeight();
////			Rectangle2D boundsTemp = newFont.getStringBounds(l, frc);
////				wText = wText < (int) boundsTemp.getWidth() + 10
////						? (int) boundsTemp.getWidth() + 10 : wText;
////			int hText = (int) boundsTemp.getHeight();
//			g.setFont(newFont);
//			g.setColor(ColorUtil.getContrastColor(fillcolor));
////			g.setColor(Color.RED);
//			g.drawString(l, x + 1, y + hgt - 4);
//			g.setFont(font);
//		}
//		
//		if(linecolor != null){
//			Graphics2D g2d = (Graphics2D) g;
//			g2d.setStroke(new BasicStroke(2));
//			g.setColor(linecolor);
//			g.drawRect(x, y, width - 1, height -1);
////			g.drawRoundRect(x, y, width, height, 5, 5);
//		}
//
//		if (isSelected) {
//			// Draw a red line on the bound of the rectangle. 
//			g.setColor(Color.RED);
//			g.drawRect(x, y, width, height);
////			g.drawRoundRect(x, y, width, height, 5, 5);
//		}
//		if (isCenter) {
//			// Draw a filled center in the rectangle. 
//			drawCenterRect(g);
//		}
//		if (isAnti) {
////			Graphics2D g2d = (Graphics2D) g;
////			g2d.setStroke(new BasicStroke(1));
////			g2d.setColor(ColorUtil.getContrastColor(fillcolor));
////			g2d.draw(new Line2D.Float(x, y, x + width, y + height));
////			g2d.draw(new Line2D.Float(x, y + height, x + width, y));
//			drawCenterRect(g);
//		}
//
//		g.setColor(orgColor);
//	}
//
//	private void drawCenterRect(Graphics g) {
//		g.setColor(ColorUtil.getContrastColor(fillcolor));
//		int centerwidth = width / 8;
//		int centerheight = height / 8;
//		g.fillRect(x + 3 * centerwidth, y + 3 * centerheight, width - 6 * centerwidth,
//				height - 6 * centerheight);
//	}
//
//	public boolean isInTile(int coordX, int coordY) {
//		if (this.x <= coordX && coordX <= this.x + width) {
//			if (this.y <= coordY && coordY <= this.y + height) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	public Color getColor() {
//		return fillcolor;
//	}
//
//	public void setColor(Color color) {
//		this.fillcolor = color;
//	}
//	
//	public Color getLinecolor() {
//		return linecolor;
//	}
//
//	public void setLineColor(Color linecolor) {
//		this.linecolor = linecolor;
//	}
//	
//
//	public boolean isSelected() {
//		return isSelected;
//	}
//
//	public void setSelected(boolean isSelected) {
//		this.isSelected = isSelected;
//	}
//
//	public boolean isCenter() {
//		return isCenter;
//	}
//
//	public void setCenter(boolean setCenter) {
//		this.isCenter = setCenter;
//	}
//
//	public boolean isAnti() {
//		return isAnti;
//	}
//
//	public void setAnti(boolean b) {
//		this.isAnti = b;
//	}
//
//	public int getX() {
//		return x;
//	}
//
//	public void setX(int x) {
//		this.x = x;
//	}
//
//	public int getY() {
//		return y;
//	}
//
//	public void setY(int y) {
//		this.y = y;
//	}
//
//	public int getWidth() {
//		return width;
//	}
//
//	public void setWidth(int width) {
//		this.width = width;
//	}
//
//	public int getHeight() {
//		return height;
//	}
//
//	public void setHeight(int height) {
//		this.height = height;
//	}
//
//	public int getLabelLetters() {
//		return labelLetters;
//	}
//
//	public void setLabelLetters(int labelLetters) {
//		this.labelLetters = labelLetters;
//	}

}
