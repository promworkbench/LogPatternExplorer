package org.processmining.lip.model;

public class TileSize {

	final public static int DEFAULT_WIDTH_MARGIN = 2;
	final public static int DEFAULT_HEIGHT_MARGIN = 2;
	final public static int DEFAULT_TILE_WIDTH = 10;
	final public static int DEFAULT_TILE_HEIGHT = 10;
	final public static int DEFAULT_TILE_SPACE = 2;

	public int widthMargin;
	public int heightMargin;
	public int tileWidth;
	public int tileHeight;
	public int tileSpace;
	
	// If -1, full label is shown. If 0, no label is shown. 
	public int tileLabelLetter;

	public TileSize() {
		this(DEFAULT_WIDTH_MARGIN, DEFAULT_HEIGHT_MARGIN, DEFAULT_TILE_WIDTH,
				DEFAULT_TILE_HEIGHT, DEFAULT_TILE_SPACE, 0);
	}

	public TileSize(int widthMargin, int heightMargin, int tileWidth, int tileHeight,
			int tileSpace, int tileLabelLetter) {
		this.widthMargin = widthMargin;
		this.heightMargin = heightMargin;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.tileSpace = tileSpace;
		this.tileLabelLetter = tileLabelLetter;
	}
}