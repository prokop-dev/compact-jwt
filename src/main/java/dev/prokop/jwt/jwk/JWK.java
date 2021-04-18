package dev.prokop.jwt.jwk;

import dev.prokop.jwt.tools.JsonToMapParser;

import java.util.Map;

/**
 * https://tools.ietf.org/html/rfc7517
 */
public interface JWK {

    static JWK parse(String json) {
        final Map<String, Object> parse = JsonToMapParser.parse(json);
        return new JWKImpl(parse);
    }

    Map<String, Object> internal();

    class JWKImpl implements JWK {

        private final Map<String, Object> internal;

        private JWKImpl(Map<String, Object> internal) {
            this.internal = internal;
        }

        @Override
        public Map<String, Object> internal() {
            return internal;
        }
    }

}