package gov.iti.jets.NutriScan.util;

import gov.iti.jets.NutriScan.exception.ImageTooLargeException;
import gov.iti.jets.NutriScan.exception.InvalidImageException;
import gov.iti.jets.NutriScan.exception.NoImageProvidedException;
import org.springframework.web.multipart.MultipartFile;

public class ImageValidationUtils {

    private ImageValidationUtils() {
    }

    public static void validateImage(MultipartFile image) {
        final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;

        if (image == null || image.isEmpty())
            throw new NoImageProvidedException("Image is required");

        String contentType = image.getContentType();

        if (contentType == null || !contentType.startsWith("image/"))
            throw new InvalidImageException("Only image files are allowed");

        if (image.getSize() > MAX_IMAGE_SIZE_BYTES)
            throw new ImageTooLargeException("Image size must not exceed 5 MB");

    }
}
