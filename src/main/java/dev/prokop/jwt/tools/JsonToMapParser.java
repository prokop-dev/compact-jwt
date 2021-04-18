package dev.prokop.jwt.tools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

public class JsonToMapParser {

    public static final int FIRST = 0;
    public static final int CURRENT = 1;
    public static final int NEXT = 2;
    private static final Object OBJECT_END = "}";
    private static final Object ARRAY_END = "]";
    private static final Object OBJECT_START = "{";
    private static final Object ARRAY_START = "[";
    private static final Object COLON = ":";
    private static final Object COMMA = ",";
    private static final HashSet<Object> PUNCTUATION = new HashSet<Object>(
            Arrays.asList(OBJECT_END, OBJECT_START, ARRAY_END, ARRAY_START, COLON, COMMA));
    private static final Map<Character, Character> escapes = new HashMap<Character, Character>();

    static {
        escapes.put(new Character('"'), new Character('"'));
        escapes.put(new Character('\\'), new Character('\\'));
        escapes.put(new Character('/'), new Character('/'));
        escapes.put(new Character('b'), new Character('\b'));
        escapes.put(new Character('f'), new Character('\f'));
        escapes.put(new Character('n'), new Character('\n'));
        escapes.put(new Character('r'), new Character('\r'));
        escapes.put(new Character('t'), new Character('\t'));
    }

    private CharacterIterator it;
    private char c;
    private Object token;
    private final StringBuffer buf = new StringBuffer();

    /**
     *
     * @param jsonAsString
     * @return
     */
    public static Map<String, Object> parse(String jsonAsString) {
        return (Map<String, Object>) new JsonToMapParser().read(jsonAsString);
    }

    private char next() {
        if (it.getIndex() == it.getEndIndex())
            throw new MalformedJsonException("Reached end of input at the " +
                    it.getIndex() + "th character.");
        c = it.next();
        return c;
    }

    private char previous() {
        c = it.previous();
        return c;
    }

    private void skipWhiteSpace() {
        do {
            if (Character.isWhitespace(c))
                ;
            else if (c == '/') {
                next();
                if (c == '*') {
                    // skip multiline comments
                    while (c != CharacterIterator.DONE)
                        if (next() == '*' && next() == '/')
                            break;
                    if (c == CharacterIterator.DONE)
                        throw new MalformedJsonException("Unterminated comment while parsing JSON string.");
                } else if (c == '/')
                    while (c != '\n' && c != CharacterIterator.DONE)
                        next();
                else {
                    previous();
                    break;
                }
            } else
                break;
        } while (next() != CharacterIterator.DONE);
    }

    public Object read(CharacterIterator ci, int start) {
        it = ci;
        switch (start) {
            case FIRST:
                c = it.first();
                break;
            case CURRENT:
                c = it.current();
                break;
            case NEXT:
                c = it.next();
                break;
        }
        return read();
    }

    public Object read(CharacterIterator it) {
        return read(it, NEXT);
    }

    public Object read(String string) {
        return read(new StringCharacterIterator(string), FIRST);
    }

    private void expected(Object expectedToken, Object actual) {
        if (expectedToken != actual)
            throw new MalformedJsonException("Expected " + expectedToken + ", but got " + actual + " instead");
    }

    @SuppressWarnings("unchecked")
    private <T> T read() {
        skipWhiteSpace();
        char ch = c;
        next();
        switch (ch) {
            case '"':
                token = readString();
                break;
            case '[':
                token = readArray();
                break;
            case ']':
                token = ARRAY_END;
                break;
            case ',':
                token = COMMA;
                break;
            case '{':
                token = readObject();
                break;
            case '}':
                token = OBJECT_END;
                break;
            case ':':
                token = COLON;
                break;
            case 't':
                if (c != 'r' || next() != 'u' || next() != 'e')
                    throw new MalformedJsonException("Invalid JSON token: expected 'true' keyword.");
                next();
                token = Boolean.TRUE;
                break;
            case 'f':
                if (c != 'a' || next() != 'l' || next() != 's' || next() != 'e')
                    throw new MalformedJsonException("Invalid JSON token: expected 'false' keyword.");
                next();
                token = Boolean.FALSE;
                break;
            case 'n':
                if (c != 'u' || next() != 'l' || next() != 'l')
                    throw new MalformedJsonException("Invalid JSON token: expected 'null' keyword.");
                next();
                token = null;
                break;
            default:
                c = it.previous();
                if (Character.isDigit(c) || c == '-') {
                    token = readNumber();
                } else throw new MalformedJsonException("Invalid JSON near position: " + it.getIndex());
        }
        return (T) token;
    }

    private String readObjectKey() {
        Object key = read();
        if (key == null)
            throw new MalformedJsonException("Missing object key (don't forget to put quotes!).");
        else if (key == OBJECT_END)
            return null;
        else if (PUNCTUATION.contains(key))
            throw new MalformedJsonException("Missing object key, found: " + key);
        else
            return key.toString();
    }

    private Map<String, Object> readObject() {
        Map<String, Object> ret = new HashMap<>();
        String key = readObjectKey();
        while (token != OBJECT_END) {
            expected(COLON, read()); // should be a colon
            if (token != OBJECT_END) {
                Object value = read();
                ret.put(key, value);
                if (read() == COMMA) {
                    key = readObjectKey();
                    if (key == null || PUNCTUATION.contains(key))
                        throw new MalformedJsonException("Expected a property name, but found: " + key);
                } else
                    expected(OBJECT_END, token);
            }
        }
        return ret;
    }

    private List<Object> readArray() {
        List<Object> ret = new ArrayList<>();
        Object value = read();
        while (token != ARRAY_END) {
            if (PUNCTUATION.contains(value))
                throw new MalformedJsonException("Expected array element, but found: " + value);
            ret.add(value);
            if (read() == COMMA) {
                value = read();
                if (value == ARRAY_END)
                    throw new MalformedJsonException("Expected array element, but found end of array after command.");
            } else
                expected(ARRAY_END, token);
        }
        return ret;
    }

    private Object readNumber() {
        int length = 0;
        boolean isFloatingPoint = false;
        buf.setLength(0);

        if (c == '-') {
            add();
        }
        length += addDigits();
        if (c == '.') {
            add();
            length += addDigits();
            isFloatingPoint = true;
        }
        if (c == 'e' || c == 'E') {
            add();
            if (c == '+' || c == '-') {
                add();
            }
            addDigits();
            isFloatingPoint = true;
        }

        String s = buf.toString();
        Number n = isFloatingPoint
                ? (length < 17) ? Double.valueOf(s) : new BigDecimal(s)
                : (length < 20) ? Long.valueOf(s) : new BigInteger(s);
        return n;
    }

    private int addDigits() {
        int ret;
        for (ret = 0; Character.isDigit(c); ++ret) {
            add();
        }
        return ret;
    }

    private String readString() {
        buf.setLength(0);
        while (c != '"') {
            if (c == '\\') {
                next();
                if (c == 'u') {
                    add(unicode());
                } else {
                    Object value = escapes.get(new Character(c));
                    if (value != null) {
                        add(((Character) value).charValue());
                    }
                }
            } else {
                add();
            }
        }
        next();
        return buf.toString();
    }

    private void add(char cc) {
        buf.append(cc);
        next();
    }

    private void add() {
        add(c);
    }

    private char unicode() {
        int value = 0;
        for (int i = 0; i < 4; ++i) {
            switch (next()) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    value = (value << 4) + c - '0';
                    break;
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                    value = (value << 4) + (c - 'a') + 10;
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                    value = (value << 4) + (c - 'A') + 10;
                    break;
            }
        }
        return (char) value;
    }

    public static class MalformedJsonException extends RuntimeException {
        public MalformedJsonException(String msg) {
            super(msg);
        }
    }

}
