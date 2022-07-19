package dev.prokop.jwt;

import org.junit.Test;

public class JwkTest {

    @Test
    public void parseEc() {
        Jwk jwk = Jwk.fromJson(TestTools.getResourceFileAsString("rfc/rfc7517/exampleJWK.json"));
        System.out.println(jwk);
    }

    @Test
    public void parseRsa() {
        Jwk jwk = Jwk.fromJson(TestTools.getResourceFileAsString("rfc/rfc7517/rsa.json"));
        System.out.println(jwk);
    }

}