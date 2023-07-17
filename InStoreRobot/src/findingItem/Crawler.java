package findingItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.net.URL;

public class Crawler {
    public Mat run(String searchQuery) {
        try {
            System.out.println("Starting google search...");
            Mat image = getFirstImageFromGoogle(searchQuery);
            System.out.println("First image for search found.");
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Search could not return image.");
        return null;
    }
    
    // Locate image url by parcing HTML code from input search. 
    public static Mat getFirstImageFromGoogle(String searchQuery) throws IOException {
    	System.out.println("Locating url...");
        String googleUrl = "https://www.google.com/search?q=" + searchQuery + "&tbm=isch";
        Document document = Jsoup.connect(googleUrl).get();
        Elements imageElements = document.select("img[data-src]");

        if (!imageElements.isEmpty()) {
            Element firstImageElement = imageElements.first();
            String imageUrl = firstImageElement.attr("abs:data-src");
            return downloadImageAndConvertToMat(imageUrl);
        }
        System.out.println("No first image found in google input search.");
        return null;
    }
    
    // Take url of search and convert image to Mat by copying buffered image data. 
    private static Mat downloadImageAndConvertToMat(String imageUrl) throws IOException {
    	System.out.println("Converting URL to Mat...");
        BufferedImage bufferedImage = ImageIO.read(new URL(imageUrl));
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Mat mat = new Mat(height, width, CvType.CV_8UC3);
        // Conversion
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }
}
