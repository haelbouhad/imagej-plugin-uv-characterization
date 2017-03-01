package fr.isima.inra;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.MessageDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Immuno_Raising_Edge implements PlugInFilter{
	
	private ImagePlus imp;

	@Override
	public void run(ImageProcessor arg0) {
		
		// Edge enhance
		ImagePlus imp = IJ.openImage("/home/hassan/Documents/ZZ3/PROJET-ANNEE/PROJET/MYSAMPLE/BAD5-B1-0001.tif");
		IJ.run(imp, "Subtract Background...", "rolling=10");
		IJ.run(imp, "Enhance Local Contrast (CLAHE)", "blocksize=127 histogram=256 maximum=3 mask=*None* fast_(less_accurate)");
		IJ.run(imp, "Options...", "iterations=1 count=8 black");
		IJ.run(imp, "Make Binary", "");
		IJ.run(imp, "Erode", "");
		imp.show();
		MessageDialog md = new MessageDialog(null, "Refine Edge Detection result", "Please use brush to close opened cells");
		
	}

	@Override
	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		return DOES_8G;
	}

}
