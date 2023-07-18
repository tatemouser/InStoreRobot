package findingItem;

import org.opencv.core.Mat;
import java.io.File;
public class Index {
    public static void main(String[] args) {
        File lib = null;
        String os = System.getProperty("os.name");
        String bitness = System.getProperty("sun.arch.data.model");
        
        if (os.toUpperCase().contains("WINDOWS")) {
            if (bitness.endsWith("64")) {
                lib = new File("libs//x64//" + System.mapLibraryName("opencv_java2411"));
            } else {
                lib = new File("libs//x86//" + System.mapLibraryName("opencv_java2411"));
            }
        }
		
        System.out.println(lib.getAbsolutePath());
        System.load(lib.getAbsolutePath());

        System.out.println("Started....");
        System.out.println("Loading images...");
        
        
        
        // Enter search keywords as parameter for .run(). Scraper then finds and saves the first image from google with this input.
        Crawler scrape = new Crawler();
        Mat item = scrape.run("Kroger Hamburger Helper Three Cheese Pasta & Sauce Mix Twin Pack Front");
        if (item != null) {
            DisplayImage.display(item);
        }
        
        
        // Takes in first image from google as the parameter. Then removes unnecessary background image details.
        RemoveBackground test = new RemoveBackground(item);
        Mat croppedItemImg = test.removeBackground();
        if (croppedItemImg != null) {
            DisplayImage.display(croppedItemImg);
        }
        
        /* Input of item with removed background and "stockShelf" within the images folder,
           Surf finds and displays region where the item image matches the stockShelf image. */
        SURFDetector surf = new SURFDetector();
        Mat shelfBox = surf.locate(croppedItemImg,"images//stockShelf.jpg");
        if (item != null) {
            DisplayImage.display(shelfBox);
        }
    }
}
