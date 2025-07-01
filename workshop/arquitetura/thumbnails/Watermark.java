package workshop.arquitetura.thumbnails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Component
public class Watermark {

    @Value("classpath:/deveficiente-logo-watermark-32x32.png")
    private Resource watermarkFile;
    /**
     * Cached image
     */
    private BufferedImage watermarkImage;

    @PostConstruct
    public void init() {
        try (InputStream stream = watermarkFile.getInputStream()) {
            this.watermarkImage = ImageIO.read(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Error while reading the watermark: " + e.getMessage());
        }
    }

    public BufferedImage getWatermarkImage() {
        return this.watermarkImage;
    }

}
