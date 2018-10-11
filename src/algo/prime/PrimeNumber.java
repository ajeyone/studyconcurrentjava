package algo.prime;

public class PrimeNumber {
    private static final int VALUE_IS_PRIME = 0;
    private static final int VALUE_NOT_PRIME = 1;

    private final int[] primes;

    /**
     * PrimeNumber mainly is used to check if number is prime. Using Sieve of
     * Eratosthenes algorithm.
     * 
     * @param max The max number to handle.
     */
    public PrimeNumber(int max) {
        if (max <= 1) {
            throw new IllegalArgumentException("max should be greater than 1");
        }
        primes = new int[max + 1];
        primes[0] = primes[1] = VALUE_NOT_PRIME;
        primes[2] = VALUE_IS_PRIME;
        int M = (int) Math.sqrt(max);
        for (int i = 2; i <= M; i++) {
            if (primes[i] == VALUE_IS_PRIME) {
                for (int j = i * i; j <= max; j += i) {
                    primes[j] = VALUE_NOT_PRIME;
                }
            }
        }
    }

    /**
     * Check if the number is prime. Result is not reliable if number is greater
     * than the max number.
     * 
     * @param number
     * @return
     */
    public boolean isPrimeNumber(int number) {
        if (number < primes.length) {
            return primes[number] == VALUE_IS_PRIME;
        }
        return false;
    }

    /**
     * Find the smallest prime number greater than n.
     * 
     * @param n Should in range [Integer.MIN_VALUE, max]
     * @return The next prime number; 0 if no prime number in range (n, max]
     */
    public int nextPrime(int n) {
        final int length = primes.length;
        if (n >= length) {
            return 0;
        }
        if (n < 0) {
            n = 0;
        }
        do {
            n++;
        } while (n < length && primes[n] == VALUE_NOT_PRIME);
        return n == length ? 0 : n;
    }
}
