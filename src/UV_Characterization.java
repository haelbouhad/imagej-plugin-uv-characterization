

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Scrollbar;
import java.awt.TextField;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Scrollable;

import ij.*;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.HistogramWindow;
import ij.gui.NewImage;
import ij.gui.NonBlockingGenericDialog;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.*;
import ij.plugin.filter.PlugInFilter;
import ij.plugin.frame.RoiManager;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class UV_Characterization implements PlugInFilter, DialogListener {
	
	private RoiManager rm;
	
	private HistogramWindow histo;
	
	private List<Measurment> measurments;
	
	private ImagePlus imp;
	
	private int nbClasses;
	
	private List<Classe> classes;
	
	
	private int threshold[];

	public UV_Characterization() {
		
		measurments = new ArrayList<Measurment>();
		
		classes = new ArrayList<Classe>();
		
		threshold = new int[2];
		
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
    
        	
        	
    		rm = RoiManager.getRoiManager();
    		
    		
    		ResultsTable measurement =  null;
    		
    		for(int i = 0; i < rm.getCount() ; ++i){
    			
    			rm.select(imp, i);		
    			
    			measurement = rm.multiMeasure(imp);
    			
    			measurments.add(new Measurment(i, 
    									    measurement.getLabel(0), 
    										measurement.getStringValue(0, 0), 
    										measurement.getStringValue(1, 0), 
    										measurement.getStringValue(2, 0), 
    										measurement.getStringValue(3, 0)
    							));
    			
    			
    		}
    		
    		imp.show();
    		
    		
    		// Histogram 
    		makeHistogram();
    		
    		// Determine number of classes
    		showNumberOfClassesDialog();
    		
    		// Choose threshold of each class
    		NonBlockingGenericDialog dial2 = null;
    		switch (nbClasses) {
				case 2:
		    		showOneThresholdSlider(dial2);
					break;

				case 3:
		    		showTwoThresholdSlider(dial2);
					break;
			}

    		
    		
	    	
			/*roi.setSelectedIndexes(roi.getIndexes());
			roi.runCommand("Set Fill Color", "yellow");
			
			roi.runCommand(imp,"Show All");
			roi.runCommand(imp,"Show All with labels");
			roi.deselect();
			roi.select(0);
			roi.runCommand("Set Fill Color", "red"); */
			
			
			
			
    		
    		
	}

	private void makeHistogram() {
		
		ImagePlus imp3 = NewImage.createFloatImage("means image", measurments.size(), 1, 1, 1);
		
		FloatProcessor fp = (FloatProcessor) imp3.getProcessor();
		
		for(Measurment measurment : measurments){
			fp.setf(measurment.getId(), 0, measurment.getMean());
			IJ.log(measurment.getId() + " - " + 0 +" - " + measurment.getMean());
		}
		
		histo = new HistogramWindow("Means histogram", imp3, 256);
		
	}

	private void showNumberOfClassesDialog() {
		
		NonBlockingGenericDialog dial1 = new NonBlockingGenericDialog("Segmentation classes");			
		dial1.addSlider ("Number of classes", 1,3,2);
		dial1.addDialogListener(this);
		dial1.showDialog();
		if(dial1.wasCanceled()){
			histo.close();
			rm.close();
			return;
		}
		
	}

	private void showTwoThresholdSlider(NonBlockingGenericDialog dial2) {
		
		dial2 = new NonBlockingGenericDialog("Threshold-2");			
		dial2.addSlider(
						"first Classes Separator value", 
						Measurment.minMean,
						Measurment.maxMean, 
						((Measurment.maxMean-Measurment.minMean)/3) + Measurment.minMean
						);
		dial2.addSlider(
						"Second Classes Separator value", 
						Measurment.minMean,
						Measurment.maxMean, 
						(2 * (Measurment.maxMean-Measurment.minMean)/3) + Measurment.minMean
						);
		dial2.addDialogListener(this);
		dial2.showDialog();
		
		if(dial2.wasCanceled()){
			return;
		}
		makeClasses();
		
	}

	private void showOneThresholdSlider(NonBlockingGenericDialog dial2) {
		dial2 = new NonBlockingGenericDialog("Threshold-1");			
		dial2.addSlider("Classes Separator value", 
						Measurment.minMean,
						Measurment.maxMean, 
						(Measurment.maxMean + Measurment.minMean)/2);
		dial2.addDialogListener(this);
		dial2.showDialog();
		
		if(dial2.wasCanceled()){
			return;
		}
		makeClasses();	
	}

	private void makeClasses() {
		classes.add(new Classe(Measurment.minMean, threshold[0]));
		for(int i = 2 ; i < nbClasses ; ++i){
			classes.add(new Classe(threshold[i-2], threshold[i-1]));
		}
		classes.add(new Classe(threshold[nbClasses-2], Measurment.maxMean));
	}

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		Vector sliders = gd.getSliders();
		switch (gd.getTitle()) {
		
			case "Segmentation classes":
				Scrollbar s = (Scrollbar)sliders.get(0);
				nbClasses = s.getValue();
				break;
				
			case "Threshold-1":
				threshold[0] = ((Scrollbar)sliders.get(0)).getValue();
				break;
			
			case "Threshold-2":
				threshold[0] = ((Scrollbar)sliders.get(0)).getValue();
				threshold[1] = ((Scrollbar)sliders.get(1)).getValue();
				if( threshold[0] >= threshold[1] ){
					((Scrollbar)sliders.get(1)).setValue(threshold[0]);
					
				}
				if( threshold[1] <= threshold[0] ){
					((Scrollbar)sliders.get(0)).setValue(threshold[1]);
				}
				break;
			}

		return true;
	}



}	


class Measurment {
	
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

class Classe {
	
	private float minThreshold;
	
	private float maxThreshold;


	public Classe(float minThreshold, float maxThrashold) {
		super();
		this.minThreshold = minThreshold;
		this.maxThreshold = maxThrashold;
		IJ.log(minThreshold + " ** " + maxThrashold );
	}

	
	
}

