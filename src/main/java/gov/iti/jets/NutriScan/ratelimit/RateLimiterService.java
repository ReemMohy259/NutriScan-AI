package gov.iti.jets.NutriScan.ratelimit;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RateLimiterService {

    private final ProxyManager<String> proxyManager;

    public ConsumptionProbe tryConsume(String key, int limit, int durationSeconds) {
        Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
            .addLimit(
                Bandwidth.builder()
                    .capacity(limit)
                    .refillGreedy(limit, Duration.ofSeconds(durationSeconds))
                    .build())
            .build();

        Bucket bucket = proxyManager.builder().build(key, configSupplier);
        return bucket.tryConsumeAndReturnRemaining(1);
    }
}