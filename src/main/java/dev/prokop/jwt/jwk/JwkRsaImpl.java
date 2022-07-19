package dev.prokop.jwt.jwk;

import dev.prokop.jwt.Jwk;
import dev.prokop.jwt.tools.Json;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class JwkRsaImpl implements Jwk, RSAPublicKey {

    private final Json json;
    private final RSAPublicKey rsaPublicKey;

    public JwkRsaImpl(Json json) {
        this.json = json;
        final PublicKey publicKey = fromJson(json);
        if (!(publicKey instanceof RSAPublicKey)) {
            throw new IllegalArgumentException("Key did not parsed into RSAPublicKey");
        }
        this.rsaPublicKey = (RSAPublicKey) publicKey;
    }

    private PublicKey fromJson(Json json) {
        final String n = json.at("n").asString();
        final String e = json.at("e").asString();

        final BigInteger N = new BigInteger(1, DECODER.decode(n));
        final BigInteger E = new BigInteger(1, DECODER.decode(e));

        try {
            final RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(N, E);
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (GeneralSecurityException gse) {
            throw new IllegalArgumentException("Can't create a key from provide modulus and exponent", gse);
        }
    }

    @Override
    public Key unwrap() {
        return rsaPublicKey;
    }

    @Override
    public String getAlgorithm() {
        return rsaPublicKey.getAlgorithm();
    }

    @Override
    public String getFormat() {
        return rsaPublicKey.getFormat();
    }

    @Override
    public byte[] getEncoded() {
        return rsaPublicKey.getEncoded();
    }

    @Override
    public BigInteger getPublicExponent() {
        return rsaPublicKey.getPublicExponent();
    }

    @Override
    public BigInteger getModulus() {
        return rsaPublicKey.getModulus();
    }

    private final static Base64.Decoder DECODER = Base64.getUrlDecoder();

}
