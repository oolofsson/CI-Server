import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

// https://stackoverflow.com/a/41156

public class RandomString {

    /**
     * Generate a random string.
     * @return String
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String lower = upper.toLowerCase(Locale.ROOT);

    private static final String digits = "0123456789";

    private static final String alphanum = upper + lower + digits;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    /**
     * Constructor of RandomString
     * @param length
     * @param random
     * @param symbols
     */
    public RandomString(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Create an alphanumeric string generator.
     * @param length
     * @param random
     */
    public RandomString(int length, Random random) {
        this(length, random, alphanum);
    }

    /**
     * Create alphanumeric strings from a secure generator.
     * @param length
     */
    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Create session identifiers.
     */
    public RandomString() {
        this(21);
    }

}
