package workshop.arquitetura.thumbnails;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.io.File;
import java.net.http.HttpHeaders;
import java.util.UUID;

@RestController
public class GetThumbnailController {

    @GetMapping(
        value="/api/users/{userId}/thumbnails/{imageId}",
        produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<Resource> getThumbnail(@PathVariable UUID userId, @PathVariable UUID imageId) {

        File file = new File("/tmp/thumbnails/" + imageId + ".png");
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Thumbnail não encontrado");
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentLength(file.length())
                .body(resource);
    }
}

