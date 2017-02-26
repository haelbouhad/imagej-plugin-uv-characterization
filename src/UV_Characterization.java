import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import ij.*;
import ij.gui.HistogramWindow;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.*;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class UV_Characterization implements PlugInFilter {
	
	private List<Measurment> measurments;
	
	private ImagePlus imp;

	public UV_Characterization() {
		
		measurments = new ArrayList<Measurment>();
		
	}
	
	public int setup(String arg, ImagePlus imp)
	{
		/*try
		{
			if (imp == null) throw (new RuntimeException());
			this.imp = imp;
		}
		catch (RuntimeException e)
		{
			IJ.error("this plugin requires an ROI image");
		}
		*/
		return DOES_RGB;
	}
	
	@Override
	public void run(ImageProcessor arg0) {
        
        	
        	IJ.open("/home/hassan/Documents/ZZ3/PROJET-ANNEE/PROJET/MYSAMPLE/SEQUENCe/SEQ/E-2/RoiSet.zip");
        	imp = IJ.openImage("/home/hassan/Documents/ZZ3/PROJET-ANNEE/PROJET/MYSAMPLE/SEQUENCe/SEQ/E-2/b-contrasted-roi-overlay.tif");
    
        	
        	
    		RoiManager roi = RoiManager.getRoiManager();
    		
    		
    		ResultsTable measurement =  null;
    		
    		for(int i = 0; i < roi.getCount() ; ++i){
    			
    			roi.select(imp, i);		
    			
    			measurement = roi.multiMeasure(imp);
    			
    			measurments.add(new Measurment(i, 
    									    measurement.getLabel(0), 
    										measurement.getStringValue(0, 0), 
    										measurement.getStringValue(1, 0), 
    										measurement.getStringValue(2, 0), 
    										measurement.getStringValue(3, 0)
    							));
    			
    		}
    		
    		imp.show();
    		
    		/* HISTOGRAMME */
    		  	
    		ImagePlus imp3 = NewImage.createFloatImage("means image", measurments.size(), 1, 1, 1);
    		
    		FloatProcessor fp = (FloatProcessor) imp3.getProcessor();
    		
    		for(Measurment measurment : measurments){
    			fp.setf(measurment.getId(), 0, measurment.getMean());
    			IJ.log(measurment.getId() + " - " + 0 +" - " + measurment.getMean());
    		}
    		
    		
    		HistogramWindow histo = new HistogramWindow("Means histogram", imp3, 256);
	    	
			/*roi.setSelectedIndexes(roi.getIndexes());
			roi.runCommand("Set Fill Color", "yellow");
			
			roi.runCommand(imp,"Show All");
			roi.runCommand(imp,"Show All with labels");
			roi.deselect();
			roi.select(0);
			roi.runCommand("Set Fill Color", "red"); */
			
			
			
			
    		
    		
	}



}	

class Measurment {
	
	private int id;
	
	private String label;
	
	private float area;
	
	private float mean;
	
	private float min;
	
	private float max;

	public Measurment(int id, String label, String area, String mean, String min, String max) {
		super();
		this.id	   = id;
		this.label = label;
		this.area  = Float.parseFloat(area);
		this.mean  = Float.parseFloat(mean);
		this.min   = Float.parseFloat(min);
		this.max   = Float.parseFloat(max);
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

	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}
	
	
	
}
