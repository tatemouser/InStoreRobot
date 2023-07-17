package findingItem;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

public class RemoveBackground {
	public Mat image;
	
	public RemoveBackground(Mat image) {
		this.image = image;
	}
	/* Find start of object on the middle of 4 sides. Use these points to get 8 more starting points of image along sides to 
	   reduce outliers. Find the largest point on each side then link the points to form 4 corners. Convert the image and remove
	   everything not within the final 4 points. */
	
	public Mat removeBackground() {
		System.out.println("Removing background of image path...");
		//image = Highgui.imread(path);

        //RemoveBackground img = new RemoveBackground(); // Create an instance
        //Mat image = img.image; // Access the non-static variable through the instance
        // Check if the image is loaded successfully
        if (image.empty()) {
            System.out.println("Error finding image to remove background.");
        }
        
        // Get the dimensions of the image
        int width = image.cols();
        int height = image.rows();
        // Create arrays to store values of boarder. Used later for linking the points and removing outside region.
        int[][] topPoints = new int[3][2];
        int[][] bottomPoints = new int[3][2];
        int[][] leftPoints = new int[3][2];
        int[][] rightPoints = new int[3][2];
        
        System.out.println("Scanning image for boarders...");
        // Get 4 main points (left, right, top, bottom) from center of sides. Used for reference in additional points. 
        leftPoints[1][0] = height/2;
        leftPoints[1][1] = findFromFront(0, width/2, height/2, 0);

        rightPoints[1][0] = height/2;
        rightPoints[1][1] = findFromBack(width-1, width/2, height/2, 0);

        topPoints[1][0] = findFromFront(0, height/2, 0, width/2);
        topPoints[1][1] = width/2; 

        bottomPoints[1][0] = findFromBack(height-1, height/2, 0, width/2);
        bottomPoints[1][1] = width/2;

        // Additional 8 points for accuracy, calls on original 4 points to find precise boarder marking.
        leftPoints[0][0] = topPoints[1][0]+((height/2-topPoints[1][0])/3);
        leftPoints[2][0] = bottomPoints[1][0]-((bottomPoints[1][0]-height/2)/3);
        rightPoints[0][0] = topPoints[1][0]+((height/2-topPoints[1][0])/3);
        rightPoints[2][0] = bottomPoints[1][0]-((bottomPoints[1][0]-height/2)/3);
        topPoints[0][1] = (width/2-leftPoints[1][1])/3+leftPoints[1][1];
        topPoints[2][1] = rightPoints[1][1]-((rightPoints[1][1]-width/2)/3);
        bottomPoints[0][1] = (width/2-leftPoints[1][1])/3+leftPoints[1][1];
        bottomPoints[2][1] = rightPoints[1][1]-((rightPoints[1][1]-width/2)/3);
        
        leftPoints[0][1] = findFromFront(0, width/2, leftPoints[0][0], 0);
        leftPoints[2][1] = findFromFront(0, width/2, leftPoints[2][0], 0);
        rightPoints[0][1] = findFromBack(width-1, width/2, rightPoints[0][0], 0);
        rightPoints[2][1] = findFromBack(width-1, width/2, rightPoints[2][0], 0);
        topPoints[0][0] = findFromFront(0, height/2, 0, topPoints[0][1]);
        topPoints[2][0] = findFromFront(0, height/2, 0, topPoints[2][1]);
        bottomPoints[0][0] = findFromBack(height-1, height/2, 0, bottomPoints[0][1]);
        bottomPoints[2][0] = findFromBack(height-1, height/2, 0, bottomPoints[2][1]);

        /* Used for seeing points when making changes to formatting.
        img.draw(leftPoints);
        img.draw(rightPoints);
        img.draw(topPoints);
        img.draw(bottomPoints); */
        
        
        // Crop image and find best points from search
        Point[] points = findPoints(leftPoints, rightPoints, topPoints, bottomPoints);

        System.out.println("Cutting image...");
        // Calculate the bounding rectangle for the points
        Rect roiRect = new Rect();
        roiRect.x = (int) points[0].x;
        roiRect.y = (int) points[0].y;
        roiRect.width = (int) (points[1].x - points[0].x);
        roiRect.height = (int) (points[3].y - points[0].y);

        // Create a new Mat to store the cropped image
        Mat croppedImage = new Mat(image, roiRect);
        System.out.println("Image has been cropped.");
        return croppedImage;
    }
    
    // Visually see points for editing code specification 
    public void draw(int[][] points) {
        for(int i = 0; i < points.length; i++) {
        	image.put(points[i][0]+1,points[i][1], 0, 0, 255);
        }
    }
    
    // Used to find a point of change in the image from the large origin, iterating backward.
    public int findFromBack(int start, int limit, int row, int col) {
    	double[] pixel;
    	for(int i = start; i > limit; i--) {
        	pixel = (row != 0 ? image.get(row, i) : image.get(i, col));
    		if (!compareColor(pixel)) {
    			if(i >= image.height()-1) i -= 1;
    			
    			return i;
        	}
    	}
    	return -1;
    }
    
    // Used to find a point of change in the image from the small origin, iterating forward.
    public int findFromFront(int start, int limit, int row, int col) {
    	double[] pixel;
    	for(int i = start; i < limit; i++) {
        	pixel = (row != 0 ? image.get(row, i) : image.get(i, col));
    		if (!compareColor(pixel)) {
    			if(i >= image.height()-1) i -= 1;
    			
    			return i;
    		}
    	}
    	return -1;
    }
    
    // See if pixel is white by checking RGB values and seeing if values are all within 25 of each other.
    private static boolean compareColor(double[] color1) {
        Arrays.sort(color1);

        double val1 = color1[0];
        double val2 = color1[1];
        double val3 = color1[2];
        
        boolean isWhite = true;
        if(val3-val1 >= 25 || val3-val2 >= 25 || val2-val1 >= 25) {
        	isWhite = false;
        }
    	return isWhite;
    }
    
    // Take in all points save and output the best four to be used in cropping outline
    public Point[] findPoints(int[][] leftPoints, int[][] rightPoints, int[][] topPoints, int[][] bottomPoints) {
    	System.out.println("Choosing best four corners of image...");
    	int[] alignmentPoints = new int[4];
    	// Find largest points to capture the entire object
        alignmentPoints[0] = leftPoints[0][1] > leftPoints[1][1] ? leftPoints[0][1] : 
        					 leftPoints[1][1] > leftPoints[2][1] ? leftPoints[1][1] : leftPoints[2][1];
        alignmentPoints[1] = rightPoints[0][1] > rightPoints[1][1] ? rightPoints[0][1] : 
        					 rightPoints[1][1] > rightPoints[2][1] ? rightPoints[1][1] : rightPoints[2][1];
        alignmentPoints[2] = topPoints[0][0] > topPoints[1][0] ? topPoints[0][0] : 
        	 				 topPoints[1][0] > topPoints[2][0] ? topPoints[1][0] : topPoints[2][0];
        alignmentPoints[3] = bottomPoints[0][0] > bottomPoints[1][0] ? bottomPoints[0][0] : 
        					 bottomPoints[1][0] > bottomPoints[2][0] ? bottomPoints[1][0] : bottomPoints[2][0];			 
		        
        Point[] points = new Point[]{
	    new Point(alignmentPoints[0], alignmentPoints[2]),
	    new Point(alignmentPoints[1], alignmentPoints[2]),
	    new Point(alignmentPoints[1], alignmentPoints[3]),
	    new Point(alignmentPoints[0], alignmentPoints[3])
        }; 
        					    	    	
    	return points;
    }
}
