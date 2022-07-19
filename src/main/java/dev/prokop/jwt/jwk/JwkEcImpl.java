package dev.prokop.jwt.jwk;

import dev.prokop.jwt.Jwk;
import dev.prokop.jwt.tools.Json;

import java.security.Key;

public class JwkEcImpl implements Jwk {

    private final Json json;
    private final Key key = null;

    public JwkEcImpl(Json json) {
        this.json = json;
    }

    @Override
    public Key unwrap() {
        return key;
    }

    @Override
    public String getAlgorithm() {
        return key.getAlgorithm();
    }

    @Override
    public String getFormat() {
        return key.getFormat();
    }

    @Override
    public byte[] getEncoded() {
        return key.getEncoded();
    }

}
