package gov.iti.jets.NutriScan.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CacheKeys {

    private CacheKeys() {
    }

    public static String barcodeSafetyKey(
        String barcode,
        List<String> allergies,
        List<String> conditions) {
        return barcode + ':' + normalize(allergies) + ':' + normalize(conditions);
    }

    private static String normalize(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .map(String::toLowerCase)
            .distinct()
            .sorted()
            .collect(Collectors.joining(","));
    }
}
