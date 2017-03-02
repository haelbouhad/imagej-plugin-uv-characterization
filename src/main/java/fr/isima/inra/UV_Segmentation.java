package fr.isima.inra;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class UV_Segmentation implements PlugInFilter {
	
	private ImagePlus imp;
	
	private String size = "0-Infinity";

	@Override
	public void run(ImageProcessor arg0) {
		
		if(!getParticleSize())
			return;
		
		ImagePlus imp2 = imp.crop();
		IJ.run("Set Measurements...", "area mean min display add redirect=None decimal=3");
		IJ.setAutoThreshold(imp2, "Default dark");
		IJ.setRawThreshold(imp2, 0, 250, null);
		IJ.run(imp2, "Analyze Particles...", "size="+size+"  show=Overlay exclude clear add");
		IJ.resetThreshold(imp2);
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
