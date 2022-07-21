package dev.prokop.jwt;

import dev.prokop.jwt.jwk.JwkEcImpl;
import dev.prokop.jwt.jwk.JwkRsaImpl;
import dev.prokop.jwt.tools.Json;

import java.security.Key;
import java.util.Base64;

/**
 * https://tools.ietf.org/html/rfc7517
 */
public interface Jwk extends Key {

    static Jwk fromJson(String jsonAsString) {
        final Json json = Json.read(jsonAsString);

        if (json.has("n") && json.has("e")) {
            return new JwkRsaImpl(json);
        }

        if (json.has("x") && json.has("y")) {
            return new JwkEcImpl(json);
        }

        throw new IllegalArgumentException("Cannot determine key type");
    }

    Key unwrap();

    /**
     * https://stackoverflow.com/questions/18039401/how-can-i-transform-between-the-two-styles-of-public-key-format-one-begin-rsa
     *
     * @return
     */
    default String pem() {
        StringBuilder pem = new StringBuilder();
        pem.append("-----BEGIN PUBLIC KEY-----").append('\n');
        pem.append(Base64.getMimeEncoder().encodeToString(getEncoded()));
        pem.append('\n').append("-----END PUBLIC KEY-----");
        return pem.toString();
    }

}
