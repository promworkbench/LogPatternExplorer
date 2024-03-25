package org.processmining.lip.visualizer.filter;

public class AttrParser {
	public static final String DELIMINATOR = " : ";

	private String attrKey;
	private String attrRegPatternParser;

	public AttrParser(String key, String parser) {
		this.attrKey = key;
		this.attrRegPatternParser = parser;
	}

	public String getAttrKey() {
		return attrKey;
	}

	public void setAttrKey(String attrKey) {
		this.attrKey = attrKey;
	}

	public String getAttrRegPatternParser() {
		return attrRegPatternParser;
	}

	public void setAttrRegPatternParser(String attrRegPatternParser) {
		this.attrRegPatternParser = attrRegPatternParser;
	}

	public String toString() {
		return attrKey + DELIMINATOR + attrRegPatternParser;
	}

}
