package gov.iti.jets.NutriScan.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryStorageService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile file) throws IOException {

        Map<?, ?> result = cloudinary.uploader()
            .upload(file.getBytes(), ObjectUtils.asMap("folder", "nutriscan"));

        return result.get("secure_url").toString();
    }

    public void delete(String publicId) throws IOException {

        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

    }
}
