package workshop.arquitetura.thumbnails;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

@RestController
public class CreateThumbnailController {

    @Autowired
    private Watermark watermark;

    @PostMapping("/api/users/{id}/thumbnails/create")
    public ResponseEntity<?> createThumbnail(@PathVariable UUID id, @Valid @RequestBody ProfilePhotoRequest photo) throws IOException {

        BufferedImage originalImage = photo.toImage();
        BufferedImage thumbnail = Thumbnails.of(originalImage)
                                            .size(200, 200)
                                            .outputFormat("png")
                                            .watermark(Positions.BOTTOM_RIGHT, watermark.getWatermarkImage(), 0.5f)
                                            .asBufferedImage();

        // TODO: escreve em disco ??

        ProfilePhotoResponse response = ProfilePhotoResponse.of(photo.getFileName(), thumbnail);
        return ResponseEntity.ok(response); // TODO: redirect para "/api/users/{id}/thumbnails/{imageId}
    }

}
