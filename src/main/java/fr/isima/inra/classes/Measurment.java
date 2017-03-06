package fr.isima.inra.classes;

public class Measurment {
	
	private int id;
	
	private String label;
	
	private float area;
	
	private float mean;
	
	private float minGreyScale;
	
	private float maxGreyScale;
	
	public static float maxMean = 0;
	
	public static float minMean = 255;

	public Measurment(int id, String label, String area, String mean, String min, String max) {
		super();
		this.id	   = id;
		this.label = label;
		this.area  = Float.parseFloat(area);
		this.mean  = Float.parseFloat(mean);
		this.minGreyScale   = Float.parseFloat(min);
		this.maxGreyScale  = Float.parseFloat(max);
		if(this.mean > maxMean) 
			maxMean = this.mean;
		if(this.mean < minMean) 
			minMean = this.mean;
	}
	
	public int getId() {
		return id;
	}

	public float getArea() {
		return area;
	}

	public void setArea(float area) {
		this.area = area;
	}

	public float getMean() {
		return mean;
	}

	public void setMean(float mean) {
		this.mean = mean;
	}

	public float getMinGreyScale() {
		return minGreyScale;
	}

	public void setMinGreyScale(float min) {
		this.minGreyScale = min;
	}

	public float getMaxGreyScale() {
		return maxGreyScale;
	}

	public void setMaxGreyScale(float max) {
		this.maxGreyScale = max;
	}
	
	
	
}