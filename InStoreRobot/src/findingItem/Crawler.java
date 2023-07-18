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
            Mat image = getMatchingImageFromGoogle(searchQuery);
            System.out.println("First image for search found.");
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Search could not return image.");
        return null;
    }
    
    // Locate image URL by parsing HTML code from input search.
    public static Mat getMatchingImageFromGoogle(String searchQuery) throws IOException {
        System.out.println("Locating URL...");
        String googleUrl = "https://www.google.com/search?q=" + searchQuery + "&tbm=isch";
        Document document = Jsoup.connect(googleUrl).get();
        Elements imageElements = document.select("img[data-src]");
        // maxAttempts represents how many image urls will be scanned when checking for "Kroger" keyword.
        int maxAttempts = 15;
        int attemptCount = 0;
        int currentIndex = 0;
        String imageUrl = null;
        // Continue until url contains keyword or reached 15 images.
        while (attemptCount < maxAttempts && currentIndex < imageElements.size()) {
            Element currentImageElement = imageElements.get(currentIndex);
            String currentImageUrl = currentImageElement.attr("abs:data-src");

            // Filter out favicon URLs
            if (currentImageUrl.contains("favicon")) {
                currentIndex++;
                continue;
            }

            if (currentImageUrl.contains("kroger")) {
                imageUrl = currentImageUrl;
                break;
            }

            attemptCount++;
            currentIndex++;
        }

        if (imageUrl != null) {
        	System.out.println("Keyword \"kroger\" was found at the " + attemptCount + " index.");
            return downloadImageAndConvertToMat(imageUrl);
        } else if (!imageElements.isEmpty()) {
            Element firstImageElement = imageElements.first();
            String firstImageUrl = firstImageElement.attr("abs:data-src");
            System.out.println("No kroger images were found, using first image.");
            return downloadImageAndConvertToMat(firstImageUrl);
        }

        System.out.println("No matching image found in the Google input search.");
        return null;
    }

    
    // Take url of search and convert image to Mat by copying buffered image data. 
    private static Mat downloadImageAndConvertToMat(String imageUrl) throws IOException {
    	System.out.println("Converting URL to Mat...");
        BufferedImage bufferedImage = ImageIO.read(new URL(imageUrl));
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Mat mat = new Mat(height, width, CvType.CV_8UC3);
        // Conversion from 
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }
}
