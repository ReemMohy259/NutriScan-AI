package gov.iti.jets.NutriScan.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryStorageService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile file) throws IOException {

        Map<?, ?> result = cloudinary.uploader()
            .upload(file.getBytes(), ObjectUtils.asMap("folder", "nutriscan"));

        return result.get("secure_url").toString();
    }

    public void delete(String publicId) throws IOException {

        // the invalidate key is to remove the image from the CDN cache as well
        Map<?, ?> result = cloudinary.uploader()
            .destroy(publicId, ObjectUtils.asMap("invalidate", true));

        log.info("Cloudinary delete response: {}", result);
    }
}
