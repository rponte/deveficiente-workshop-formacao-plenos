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
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.net.URI;

@RestController
public class CreateThumbnailController {

    @Autowired
    private Watermark watermark;

    @PostMapping("/api/users/{userId}/thumbnails/create")
    public ResponseEntity<?> createThumbnail(@PathVariable UUID userId, 
                                             @Valid @RequestBody ProfilePhotoRequest request) throws IOException {

        UUID imageId = UUID.randomUUID();
        File outputFile = new File("/tmp/thumbnails", imageId + ".png");

        BufferedImage originalImage = request.toImage();
        BufferedImage thumbnail = Thumbnails.of(originalImage)
                                            .size(200, 200)
                                            .outputFormat("png")
                                            .watermark(Positions.BOTTOM_RIGHT, watermark.getWatermarkImage(), 0.5f)
                                            .toFile(outputFile); // escreve thumbnail em disco

        String location = "/api/users/{userId}/thumbnails/{imageId}"
                                .replace("{userId}", userId.toString())
                                .replace("{imageId}", imageId.toString());
                                        
        return ResponseEntity
                    .created(URI.create(location)) // http 201
                    .build();
    }

}
