package algo.prime;

import java.util.ArrayList;

public class FactorCalculator {
    private final PrimeNumber primeNumber;

    public FactorCalculator(int maxPrimeNumber) {
        primeNumber = new PrimeNumber(maxPrimeNumber);
    }

    public int[] factor(int n) {
        if (n < 0) {
            return new int[0];
        }
        if (n < 2) {
            return new int[] { n };
        }
        ArrayList<Integer> factors = new ArrayList<>();
        int p = primeNumber.nextPrime(0);
        while (n != 1) {
            if (n % p == 0) {
                n /= p;
                factors.add(p);
            } else {
                p = primeNumber.nextPrime(p);
            }
        }
        final int length = factors.size();
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = factors.get(i);
        }
        return result;
    }
    
    public int getMaxPrimeNumber() {
        return primeNumber.getMax();
    }

    public static String buildFactorFormula(int n, int[] factors) {
        StringBuilder sb = new StringBuilder();
        if (factors == null) {
            throw new IllegalArgumentException("factors null");
        }
        sb.append(n).append(" = ");
        int length = factors.length;
        for (int i = 0; i < length; i++) {
            sb.append(factors[i]);
            if (i < length - 1) {
                sb.append(" * ");
            }
        }
        return sb.toString();
    }
}
