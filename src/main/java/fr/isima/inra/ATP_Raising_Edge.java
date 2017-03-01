package fr.isima.inra;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.WaitForUserDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import inra.ijpb.plugins.MorphologicalSegmentation;

public class ATP_Raising_Edge implements PlugInFilter {
	
	private ImagePlus imp;
	
	private ImagePlus imp2;
	
	private String name;

	private String shortName;
	
	public void run(ImageProcessor arg0) {

		ApplyLinearKuwahara();
		
		
		ApplyWaterShed();
		
		imp = WindowManager.getImage(shortName + "-watershed-lines.tif");
		if(imp != null){
			imp.show();
			WindowManager.getImage("Morphological Segmentation").close();
		}
		else{
			IJ.log("ERROR : Image not found : " + shortName + "-watershed-lines.tif");
			return;
		}
		
		ApplyErode();
		
	}
	
	private void ApplyErode() {
		IJ.run(imp, "Invert", "");
		IJ.run(imp, "Options...", "iterations=2 count=1 black do=Dilate");
		Prefs.blackBackground = true;
		IJ.run(imp, "Make Binary", "");
	}

	private void ApplyLinearKuwahara(){
		// ... 
	}


	private void ApplyWaterShed() {
		IJ.run("Morphological Segmentation");
		
		imp.hide();
		
		MorphologicalSegmentation.setInputImageType("object");
		MorphologicalSegmentation.setGradientRadius("6");
		MorphologicalSegmentation.segment("10", "true", "6");
		MorphologicalSegmentation.setDisplayFormat("Watershed lines");
		MorphologicalSegmentation.createResultImage();
		IJ.wait(3000);
		WindowManager.getImage("Morphological Segmentation").show();
		new WaitForUserDialog("Check result before clicking on 'create image' button,\n then click OK.").show();
		
	}


	@Override
	public int setup(String arg0, ImagePlus imp) {
		this.imp = imp;
		
		if(imp != null){
			name      = imp.getTitle();
			shortName      = imp.getShortTitle();
		}
		
		return DOES_8G;
	}

}


