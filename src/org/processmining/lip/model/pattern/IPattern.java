package org.processmining.lip.model.pattern;

import java.util.List;

public interface IPattern {

	public String getLabel();

	public List<String> getPredecessors();

	public List<String> getConcurrences();

	public List<String> getSuccessors();
}
