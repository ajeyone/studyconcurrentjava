package factor.exp1;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Factor {
	public static void main(String[] args) {
		exp1();
	}

	private static final int CALCULATION_COUNT = 50000;

	private static void exp1() {
		int n = Runtime.getRuntime().availableProcessors();
		System.out.println("cpu: " + n);
		Factorizer ucf = new SafeCountingFactorizer1();
		Thread[] threads = new Thread[n];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread() {
				public void run() {
					for (int j = 0; j < CALCULATION_COUNT; j++) {
						int number = new Random().nextInt(1000);
						ucf.calculateFactor(number);
					}
				}
			};
			threads[i].start();
		}

		try {
			for (int i = 0; i < threads.length; i++) {
				threads[i].join();
			}

			int correctCount = threads.length * CALCULATION_COUNT;
			System.out.println("final count=" + ucf.getCount() + ", supposed to be " + correctCount);
			System.out.println(ucf.getCount() == correctCount ? "Correct, but cannot prove it is thread safe."
					: "Wrong, it is not thread safe.");
		} catch (Throwable e) {
		}
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

	abstract public long getCount();

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

class UnsafeCountingFactorizer extends Factorizer {
	private long count = 0;

	public long getCount() {
		return count;
	}

	public int[] calculateFactor(int n) {
		int[] result = factor(n);
		count++;
		return result;
	}
}

class SafeCountingFactorizer1 extends Factorizer {
	private AtomicLong count = new AtomicLong(0);

	public long getCount() {
		return count.longValue();
	}

	public int[] calculateFactor(int n) {
		int[] result = factor(n);
		count.incrementAndGet();
		return result;
	}
}

class SafeCountingFactorizer2 extends Factorizer {
	private long count = 0;

	public long getCount() {
		return count;
	}

	public int[] calculateFactor(int n) {
		int[] result = factor(n);
		synchronized (this) {
			count++;
		}
		return result;
	}
}