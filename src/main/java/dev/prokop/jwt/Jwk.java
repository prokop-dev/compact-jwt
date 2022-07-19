package dev.prokop.jwt;

import dev.prokop.jwt.jwk.JwkEcImpl;
import dev.prokop.jwt.jwk.JwkRsaImpl;
import dev.prokop.jwt.tools.Json;

import java.security.Key;

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

}
