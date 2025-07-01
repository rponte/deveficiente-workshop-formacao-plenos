package workshop.arquitetura.thumbnails;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

public class ProfilePhotoRequest {

    public static final int MAX_FILE_SIZE = (int) ((256 * 1024) * 1.33); // ~349kb

    @NotNull
    private UUID id;

    @NotBlank
    @Size(max = 120)
    private String fileName;

    @NotBlank
    @Size(max = MAX_FILE_SIZE)
    private String photo;

    public ProfilePhotoRequest(UUID id, String fileName, String photo) {
        this.id = id;
        this.fileName = fileName;
        this.photo = photo;
    }

    public UUID getId() {
        return id;
    }
    public String getFileName() {
        return fileName;
    }
    public String getPhoto() {
        return photo;
    }

    /**
     * Converts the submitted profile photo to an instance of <code>BufferedImage</code>
     */
    public BufferedImage toImage() {
        byte[] rawPhoto = Base64.getDecoder().decode(this.photo);
        try (InputStream stream = new ByteArrayInputStream(rawPhoto)) {
            return ImageIO.read(stream);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error while reading the profile photo from request: " + e.getMessage());
        }
    }

}
