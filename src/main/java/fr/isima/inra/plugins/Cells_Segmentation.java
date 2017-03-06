package fr.isima.inra.plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Cells_Segmentation implements PlugInFilter {
	
	private ImagePlus imp;
	
	private String size = "0-Infinity";

	@Override
	public void run(ImageProcessor arg0) {
		
		// Get the size of particles (format : "0-Infinity")
		if(!getParticleSize())
			return;
		
		// Duplicate the current image
		ImagePlus imp2 = imp.crop();
		
		// Set measurment to quantify cells
		IJ.run("Set Measurements...", "area mean min display add redirect=None decimal=3");
		IJ.setAutoThreshold(imp2, "Default dark");
		
		// Select all the cells except edges
		IJ.setRawThreshold(imp2, 0, 250, null);
		
		// We choose "Overlay" to create a new image, "add" to add output to ROI manager
		IJ.run(imp2, "Analyze Particles...", "size="+size+"  show=Overlay exclude clear add");
		IJ.resetThreshold(imp2);
		
		// Display result
		imp2.show();

		
	}


    
	private Boolean getParticleSize() {
		GenericDialog gd = new GenericDialog("Analyze Particles");
		gd.addStringField("Size (pixel^2)", size);
		gd.showDialog();
		if(gd.wasCanceled()) return false;
		size = gd.getNextString();
		return true;
	}




	@Override
	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		//IJ.log(imp.getOriginalFileInfo().directory);
		return DOES_8G;
	}

}
