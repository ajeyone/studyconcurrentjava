package factor.exp2;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Factor {
	public static void main(String[] args) {
		exp1();
	}

	private static final int CALCULATION_COUNT = 50000;

	private static void exp1() {
		int n = Runtime.getRuntime().availableProcessors();
		Factorizer ucf = new SafeCachingFactorizer2();
		Thread[] threads = new Thread[n];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread() {
				public void run() {
					for (int j = 0; j < CALCULATION_COUNT; j++) {
						int number = new Random().nextInt(1000);
						int[] factors = ucf.calculateFactor(number);
						assertFactors(number, factors);
					}
				}
			};
			threads[i].start();
		}

		try {
			for (int i = 0; i < threads.length; i++) {
				threads[i].join();
			}
		} catch (Throwable e) {
		}
	}

	private static void assertFactors(int n, int[] factors) {
		int p = 1;
		for (int f : factors) {
			p *= f;
		}
		if (p != n) {
			StringBuilder sb = new StringBuilder();
			sb.append("Wrong factors: ").append(n).append(" != ");
			for (int i = 0; i < factors.length; i++) {
				sb.append(factors[i]);
				if (i < factors.length - 1) {
					sb.append(" * ");
				}
			}
			System.out.println(sb.toString());
		}
	}
}

class UnsafeCachingFactorizer1 extends Factorizer {
	private final AtomicReference<Integer> lastNumber = new AtomicReference<Integer>();
	private final AtomicReference<int[]> lastFactors = new AtomicReference<int[]>();

	public int[] calculateFactor(int n) {
		Integer last = lastNumber.get();
		if (last != null && last == n) {
			return lastFactors.get();
		} else {
			int[] factors = factor(n);
			lastNumber.set(n);
			lastFactors.set(factors);
			return factors;
		}
	}
}

class UnsafeCachingFactorizer2 extends Factorizer {
	private long lastNumber = -1;
	private int[] lastFactors;

	public int[] calculateFactor(int n) {
		if (lastNumber == n) {
			return lastFactors;
		}
		int[] factors = factor(n);
		lastNumber = n;
		lastFactors = factors;
		return factors;
	}
}

class SafeCachingFactorizer2 extends Factorizer {
	private long lastNumber = -1;
	private int[] lastFactors;

	public synchronized int[] calculateFactor(int n) {
		if (lastNumber == n) {
			return lastFactors;
		}
		int[] factors = factor(n);
		lastNumber = n;
		lastFactors = factors;
		return factors;
	}
}

class SafeCachingFactorizer1 extends Factorizer {
	private long lastNumber = -1;
	private int[] lastFactors;

	public int[] calculateFactor(int n) {
		synchronized (this) {
			if (lastNumber == n) {
				return lastFactors;
			}
		}
		int[] factors = factor(n);
		synchronized (this) {
			lastNumber = n;
			lastFactors = factors;
		}
		return factors;
	}
}

abstract class Factorizer {
	public int[] factor(int n) {
		if (n < 0) {
			return new int[0];
		}
		if (n < 2) {
			return new int[] { n };
		}
		ArrayList<Integer> factors = new ArrayList<>();
		int p = 2;
		while (n != 1) {
			if (n % p == 0) {
				n /= p;
				factors.add(p);
			} else {
				p = nextPrime(p);
			}
			// System.out.println("calc: n=" + n + ", p=" + p);
		}
		int[] result = new int[factors.size()];
		for (int i = 0; i < factors.size(); i++) {
			result[i] = factors.get(i);
		}
		return result;
	}

	abstract public int[] calculateFactor(int n);

	static final int[] sPrimes = new int[1000];
	static final int VALUE_IS_PRIME = 0;
	static final int VALUE_NOT_PRIME = 1;

	static {
		sPrimes[0] = sPrimes[1] = VALUE_NOT_PRIME;
		sPrimes[2] = VALUE_IS_PRIME;
		for (int i = 2; i < sPrimes.length; i++) {
			if (sPrimes[i] == VALUE_IS_PRIME) {
				for (int j = i + i; j < sPrimes.length; j += i) {
					sPrimes[j] = VALUE_NOT_PRIME;
				}
			}
		}
	}

	static public boolean isPrimeNumber(int n) {
		if (n < sPrimes.length) {
			return sPrimes[n] == VALUE_IS_PRIME;
		}
		return false;
	}

	static public int nextPrime(int n) {
		n++;
		while (n < sPrimes.length && sPrimes[n] == VALUE_NOT_PRIME) {
			n++;
		}
		return n;
	}
}
