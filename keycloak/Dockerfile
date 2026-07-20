# ---- Stage 1: augment Keycloak ----
FROM quay.io/keycloak/keycloak:26.6 AS builder

ENV KC_DB=postgres
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

WORKDIR /opt/keycloak
COPY keycloak/realm-config/import/ /opt/keycloak/data/import/
COPY keycloak/themes/nutriscan/ /opt/keycloak/themes/nutriscan/
COPY keycloak/spi/keycloack-email-http-provider/target/keycloak-email-http-provider.jar /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh build --spi-email-sender-provider=resend-http

# ---- Stage 2: final runtime image ----
FROM quay.io/keycloak/keycloak:26.6
COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start-dev", "--import-realm"]
