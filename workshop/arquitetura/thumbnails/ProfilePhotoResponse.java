package workshop.arquitetura.thumbnails;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ProfilePhotoResponse {

    private String fileName;
    private String thumbnail;

    private ProfilePhotoResponse(String fileName, byte[] thumbnail) {
        this.fileName = fileName;
        this.thumbnail = Base64.getEncoder().encodeToString(thumbnail);
    }

    public String getFileName() {
        return fileName;
    }
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * Creates a response payload
     */
    public static ProfilePhotoResponse of(String originalFileName, BufferedImage thumbnail) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(thumbnail, "png", baos);
            return new ProfilePhotoResponse(("thumbnail-" + originalFileName), baos.toByteArray());
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error while building response: " + e.getMessage());
        }
    }

}
