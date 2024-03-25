package org.processmining.lip.model;

public class TNodeImpl implements TNode {
	
	protected String label;
	protected int id;
	
	public TNodeImpl(){

	}
	
	public TNodeImpl(int id, String label){
		this.id = id;
		this.label = label;
	}

	public int getID() {
		return id;
	}

	public void setID(int id){
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		if(label == null){
			System.err.println("Null label");
		}
		this.label = label;
	}

}
