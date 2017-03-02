package fr.isima.inra;

import java.awt.AWTEvent;
import java.awt.Scrollbar;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.HistogramWindow;
import ij.gui.MessageDialog;
import ij.gui.NewImage;
import ij.gui.NonBlockingGenericDialog;
import ij.io.Opener;
import ij.measure.ResultsTable;
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
	
	private String[] color = { "RED" , "YELLOW" , "GREEN" };
	
	
	private int threshold[];

	private String directory;

	private String name;

	public UV_Characterization() {
		
		measurments = new ArrayList<Measurment>();
		
		classes = new ArrayList<Classe>();
		
		threshold = new int[2];
		
	}
	
	public int setup(String arg, ImagePlus imp)
	{
		
		if(imp != null){
			directory = imp.getOriginalFileInfo().directory;
			name      = imp.getTitle();
		}
		
		return DOES_8G;
	}
	
	@Override
	public void run(ImageProcessor arg0) {
        
        	// Import ROI sets file and get ROI Manager
			MessageDialog md = new MessageDialog(null, "Import ROI Sets", "Please import ROI Sets zip");
			//md.show();
			//if (md.wasCanceled()) return;
			Opener io = new Opener();
			io.open();
			rm = RoiManager.getRoiManager();
			
			// Get a duplicate image
        	imp = IJ.openImage(directory + name);
    		
    		
    		// Get measurments from roi manager
    		fillMeasurments();
    		
    		imp.show();
    		
    		
    		// Histogram 
    		makeHistogram();
    		
    		// Determine number of classes
    		if(!showNumberOfClassesDialog())
    			return;
    		
    		// Choose threshold of each class
    		NonBlockingGenericDialog dial2 = null;
    		switch (nbClasses) {
				case 2:
		    		if(!showOneThresholdSlider(dial2))
		    			return;
					break;

				case 3:
		    		if(!showTwoThresholdSlider(dial2))
		    			return;
					break;
			}

    		// Fill classes
    		makeClasses();
    		fillClasses();
    		
    		// Color Cells
    		colorClasses();			
			
    		// Show Result
    		rm.close();
    		histo.close();
    		imp.show();
  
	}
	
	public void fillMeasurments(){
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
	}

	public void colorClasses(){
    	for(Classe classe : classes){
    		rm.setSelectedIndexes(classe.getIndexes());
			rm.runCommand("Set Fill Color", color[classes.indexOf(classe)]);
			rm.runCommand(imp,"Show All");
			rm.runCommand(imp,"Show All with labels");
    	}
	}
	
	private void fillClasses() {
		
		for(Measurment measurment : measurments){
			float mean = measurment.getMean();
			for( int i = 0 ; i < nbClasses ; ++i){
				if( classes.get(i).getMinThreshold() <= mean  && mean < classes.get(i).getMaxThreshold() ){
					classes.get(i).addIndex(measurment.getId());
					//IJ.log(classes.get(i).getMinThreshold() + " - " + mean + " - " + classes.get(i).getMaxThreshold());
				}
			}
		}
		
		
	}

	private void makeHistogram() {
		
		ImagePlus imp3 = NewImage.createFloatImage("means image", measurments.size(), 1, 1, 1);
		
		FloatProcessor fp = (FloatProcessor) imp3.getProcessor();
		
		for(Measurment measurment : measurments){
			fp.setf(measurment.getId(), 0, measurment.getMean());
			//IJ.log(measurment.getId() + " - " + 0 +" - " + measurment.getMean());
		}
		
		histo = new HistogramWindow("Means histogram", imp3, 256);
		
	}

	private Boolean showNumberOfClassesDialog() {
		
		NonBlockingGenericDialog dial1 = new NonBlockingGenericDialog("Segmentation classes");			
		dial1.addSlider ("Number of classes", 1,3,2);
		dial1.addDialogListener(this);
		dial1.showDialog();
		if(dial1.wasCanceled()){
			closeProcess();
			return false;
		}
		return true;
	}
	

	private Boolean showTwoThresholdSlider(NonBlockingGenericDialog dial2) {
		
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
			closeProcess();
			return false;
		}
		return true;
		
	}
	
	void closeProcess(){
		histo.close();
		rm.close();
		imp.close();
	}

	private Boolean showOneThresholdSlider(NonBlockingGenericDialog dial2) {
		dial2 = new NonBlockingGenericDialog("Threshold-1");			
		dial2.addSlider("Classes Separator value", 
						Measurment.minMean,
						Measurment.maxMean, 
						(Measurment.maxMean + Measurment.minMean)/2);
		dial2.addDialogListener(this);
		dial2.showDialog();
		
		if(dial2.wasCanceled()){
			closeProcess();
			return false;
		}
		
		return true;
			
	}

	private void makeClasses() {
		
		if(nbClasses == 1){
			classes.add(new Classe(Measurment.minMean, Measurment.maxMean));
		}else{
			classes.add(new Classe(Measurment.minMean, threshold[0]));
			for(int i = 2 ; i < nbClasses ; ++i){
				classes.add(new Classe(threshold[i-2], threshold[i-1]));
			}
			classes.add(new Classe(threshold[nbClasses-2], Measurment.maxMean));
		}
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

	
	public void imageClosed(ImagePlus arg0) {
		
		if(histo != null )
			histo.close();
		if(rm != null)
			rm.close();
		
	}

	
	public void imageOpened(ImagePlus arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void imageUpdated(ImagePlus arg0) {
		// TODO Auto-generated method stub
		
	}



}	






