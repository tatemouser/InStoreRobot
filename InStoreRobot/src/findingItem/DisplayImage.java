package findingItem;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.opencv.core.Mat;

public class DisplayImage {
    
	public static void display(Mat image) {
	    String windowName = "image";
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int maxWidth = (int) screenSize.getWidth() - 100;
	    int maxHeight = (int) screenSize.getHeight() - 100;
	    int imgWidth = image.cols();
	    int imgHeight = image.rows();
	    int newWidth = imgWidth;
	    int newHeight = imgHeight;
	    if (imgWidth > maxWidth || imgHeight > maxHeight) {
	        double widthRatio = (double) maxWidth / imgWidth;
	        double heightRatio = (double) maxHeight / imgHeight;
	        double scaleFactor = Math.min(widthRatio, heightRatio);
	        newWidth = (int) (imgWidth * scaleFactor);
	        newHeight = (int) (imgHeight * scaleFactor);
	    }
	    Image resizedImage = toBufferedImage(image).getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
	    ImageIcon icon = new ImageIcon(resizedImage);
	    JFrame frame = new JFrame(windowName);
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    JLabel lbl = new JLabel();
	    lbl.setIcon(icon);
	    frame.add(lbl);
	    frame.pack();
	    frame.setLocationRelativeTo(null); // Center the frame on the screen
	    frame.setVisible(true);
	}

	private static BufferedImage toBufferedImage(Mat mat) {
	    int type = BufferedImage.TYPE_BYTE_GRAY;
	    if (mat.channels() > 1) {
	        type = BufferedImage.TYPE_3BYTE_BGR;
	    }
	    int bufferSize = mat.channels() * mat.cols() * mat.rows();
	    byte[] buffer = new byte[bufferSize];
	    mat.get(0, 0, buffer); // Get all the pixels
	    BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
	    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	    System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
	    return image;
	}

}
