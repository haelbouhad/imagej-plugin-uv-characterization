package fr.isima.inra;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class UV_Segmenatation implements PlugInFilter {
	
	private ImagePlus imp;

	@Override
	public void run(ImageProcessor arg0) {
		
		ImagePlus imp2 = imp.crop();
		IJ.run("Set Measurements...", "area mean min display add redirect=None decimal=3");
		IJ.setAutoThreshold(imp2, "Default dark");
		IJ.setRawThreshold(imp2, 0, 250, null);
		IJ.run(imp2, "Analyze Particles...", "  show=Overlay exclude clear add");
		IJ.resetThreshold(imp2);
		imp2.show();
		
	}


    
	@Override
	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		//IJ.log(imp.getOriginalFileInfo().directory);
		return DOES_8G;
	}

}
