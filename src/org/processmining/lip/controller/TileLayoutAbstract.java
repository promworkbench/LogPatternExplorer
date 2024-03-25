package org.processmining.lip.controller;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XTrace;
import org.processmining.lip.model.Tile;
import org.processmining.lip.model.TileSize;
import org.processmining.lip.model.Tiles;

public abstract class TileLayoutAbstract implements TileLayout {

	private XEventClassifier _classifier;
	
	public Tiles computeTiles(XTrace trace, TileSize tileSize) {
		 Tiles ts = computeTiles(trace, tileSize.widthMargin, tileSize.heightMargin,
				tileSize.tileWidth, tileSize.tileHeight, tileSize.tileSpace);
		 
		 for(Tile t : ts.getTiles()){
			 t.setLabelLetters(tileSize.tileLabelLetter);
		 }
		 return ts;
	}
	
	
	public XEventClassifier getClassifier(){
		if(_classifier == null){
			return new XEventNameClassifier();
		}
		return _classifier;
	}
	
	public void setClassifier(XEventClassifier classifier){
		_classifier = classifier;
	}


}
