package fr.isima.inra.plugins;

import java.awt.AWTEvent;
import java.awt.Label;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.morphology.MinimaAndMaxima3D;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Strel3D;
import inra.ijpb.watershed.Watershed;

public class ATP_Raising_Edge implements PlugInFilter, DialogListener {
	
	private ImagePlus imp;
	
	private ImagePlus imp2;
	
	private ImagePlus temporary = new ImagePlus();
	
	private String name;

	private String shortName;
	
	int dynamic = 10;
	int connectivity = 6;
	int gradientRadius = 6;
	boolean calculateDams = true;
	boolean preview = false;

	private String message = "";

	private ImagePlus addResult = new ImagePlus();
	
	
	public void run(ImageProcessor arg0) {

		if(!getThresholdParam())
			return;
		
		//ApplyLinearKuwahara();
		
		
		//ApplyWaterShed();
		

		
		//ApplyErode();
		
		

	}
	

	private boolean getThresholdParam() {
		GenericDialog gd = new GenericDialog("WaterShed Parameters");
		gd.addStringField("Dynamic", dynamic + "");
		gd.addStringField("Connectivity", connectivity + "");
		gd.addStringField("Gradient radius", gradientRadius + "");
		gd.addCheckbox("calculateDams", calculateDams);
		gd.addCheckbox("Preview", preview);
		gd.addMessage(message );
		gd.addDialogListener(this);
		gd.showDialog();
		return true;
	}
	
	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent arg1) {
		
		if (gd.wasCanceled()){
			temporary.changes = false; temporary.close();
			addResult.changes = false; addResult.close();
			return false;
		}
		dynamic = Integer.parseInt(gd.getNextString());
		connectivity = Integer.parseInt(gd.getNextString());
		gradientRadius = Integer.parseInt(gd.getNextString());
		calculateDams = gd.getNextBoolean();
		preview = gd.getNextBoolean();
		if(preview){
			((Label)gd.getMessage()).setText("Please wait, Processing ... ");
			ApplyWaterShed();
			ImageCalculator ic = new ImageCalculator();
			addResult = ic.run("Add create", temporary, imp);
			((Label)gd.getMessage()).setText("");
			temporary.hide();
			addResult.show();
		}else{
			temporary.changes = false; temporary.close();
			addResult.changes = false; addResult.close();
		}
		return true;
		
	}


	private void ApplyErode() {
		IJ.run(imp, "Invert", "");
		IJ.run(imp, "Options...", "iterations=2 count=1 black do=Dilate");
		Prefs.blackBackground = true;
		IJ.run(imp, "Make Binary", "");
	}

	private void ApplyLinearKuwahara(){
		ImagePlus imp2 = imp.crop();
		imp2.show();
		IJ.run(imp2, "Linear Kuwahara", "number_of_angles=20 line_length=11 criterion=Variance");
		IJ.run(imp2, "Linear Kuwahara", "number_of_angles=20 line_length=11 criterion=Variance");
		imp2.show();
	}


	private void ApplyWaterShed() {
		if(temporary != null){
			temporary.hide();
			addResult.hide();
		}
		temporary = segmentImage(imp, dynamic, connectivity, gradientRadius, calculateDams);
		temporary = getWatershedLines( temporary );
		temporary.show();
	}
	
	ImagePlus segmentImage( 
			ImagePlus input, 
			int dynamic, 
			int connectivity,
			int gradientRadius,
			boolean calculateDams )
	{
		Strel3D strel = Strel3D.Shape.CUBE.fromRadius( gradientRadius );
		ImageStack image = Morphology.gradient( input.getImageStack(), strel );
		ImageStack regionalMinima = MinimaAndMaxima3D.extendedMinima( image, dynamic, connectivity );
		ImageStack imposedMinima = MinimaAndMaxima3D.imposeMinima( image, regionalMinima, connectivity );
		ImageStack labeledMinima = BinaryImages.componentsLabeling( regionalMinima, connectivity, 32 );
		ImageStack resultStack = Watershed.computeWatershed( imposedMinima, labeledMinima, 
				connectivity, calculateDams );
		ImagePlus resultImage = new ImagePlus( "watershed", resultStack );
		resultImage.setCalibration( input.getCalibration() );
		return resultImage;
	}
	
	private ImagePlus getWatershedLines( ImagePlus labels )
	{
		final ImagePlus lines = BinaryImages.binarize( labels );
		IJ.run( lines, "Invert", "stack" );
		return lines;
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


