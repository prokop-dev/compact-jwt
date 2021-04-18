package dev.prokop.jwt.jwk;

import dev.prokop.jwt.TestTools;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JWKTest {

    @Test
    void parseExample1() {
        final JWK jwk = JWK.parse(TestTools.getResourceFileAsString("rfc/rfc7517/exampleJWK.json"));
        final Map<String, Object> internal = jwk.internal();
        assertEquals("EC", internal.get("kty"));
        assertEquals("P-256", internal.get("crv"));
        assertEquals("f83OJ3D2xF1Bg8vub9tLe1gHMzV76e8Tus9uPHvRVEU", internal.get("x"));
        assertEquals("x_FEzRu9m36HLN_tue659LNpXW6pCyStikYjKIWI5a0", internal.get("y"));
        assertEquals("Public key used in JWS spec Appendix A.3 example", internal.get("kid"));
    }

}