package fr.isima.inra.classes;

import java.util.ArrayList;
import java.util.List;

public class Classe {
	
	private float minThreshold;
	
	private float maxThreshold;
	
	private List<Integer> indexes;


	public int[] getIndexes() {
		int[] tab = new int[indexes.size()];
		for(int i = 0 ; i < tab.length ; ++i)
			tab[i] = indexes.get(i);
		return tab;
	}


	public Classe(float minThreshold, float maxThrashold) {
		super();
		this.setMinThreshold(minThreshold);
		this.setMaxThreshold(maxThrashold);
		indexes = new ArrayList<Integer>();
	}


	public void addIndex(int id) {
		indexes.add(id);
	}


	public float getMinThreshold() {
		return minThreshold;
	}


	public void setMinThreshold(float minThreshold) {
		this.minThreshold = minThreshold;
	}


	public float getMaxThreshold() {
		return maxThreshold;
	}


	public void setMaxThreshold(float maxThreshold) {
		this.maxThreshold = maxThreshold;
	}

	@Override
	public String toString() {
		String str = "[ Min : " + minThreshold + " - Max : " + maxThreshold + "\n  Indexes = {";
		for(Integer index : indexes)
			str += index.toString() + ", ";
		str += "}\n]";
		return str;
	}
	
}

