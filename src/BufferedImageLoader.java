import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BufferedImageLoader {

    private BufferedImage img;

    public BufferedImage loadImage(String path){
        try {
            img = ImageIO.read(getClass().getResource(path));
        }catch(IOException e){
            return null;
        }
        return img;
    }
}
