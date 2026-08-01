package gov.iti.jets.NutriScan.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

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

    public String uploadOrUpdateFamilyMember(MultipartFile file, UUID familyMemberId)
        throws IOException {

        Map<?, ?> result = cloudinary.uploader()
            .upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder",
                    "nutriscan/family-members",
                    "public_id",
                    familyMemberId.toString(),
                    "overwrite",
                    true,
                    "invalidate",
                    true));

        return result.get("secure_url").toString();
    }
    public String uploadOrUpdateUserProfile(MultipartFile file, UUID userId) throws IOException {

        Map<?, ?> result = cloudinary.uploader()
            .upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder",
                    "nutriscan/users",
                    "public_id",
                    userId.toString(),
                    "overwrite",
                    true,
                    "invalidate",
                    true));

        return result.get("secure_url").toString();
    }

}
