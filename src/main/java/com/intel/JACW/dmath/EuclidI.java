package com.intel.JACW.dmath;

import com.intel.mttest.representation.AbstractTestCase;

/**
 * <p>
 * Test calculates GCD of [integer] for various pairs of numbers multiple times.
 * </p>
 * <p>
 * It very suitable for solving diophantine equations, building continued
 * fractions, factorization (most effective algoritm - SQUFOF)
 * </p>
 * Where it is used in android applications:
 * <ol>
 * <li>Cryptographic algorithm with public key called RSA</li>
 * <li>Applications which securely store data (for example: <a
 * href="http://www.cryptia.org/">Cryptia</a>,<a
 * href="https://play.google.com/store/apps/details?id=com.gs.saveit">Password
 * Vault</a>, ...)</li>
 * </ol>
 * 
 * 
 * @see <a
 *      href="https://en.wikipedia.org/wiki/Shanks%27_square_forms_factorization">SQUFOF
 *      (Wikipedia)</a>
 * 
 */
public class EuclidI extends AbstractTestCase {

	@Override
	public long iteration() {
		long counter = 0;
		for (int i = 0; i < repeats; i++) {
			int a = i + 1;
			int b = repeats - i;
			while (a != b) {
				if (a > b)
					a = a - b;
				else
					b = b - a;
				counter += 1;
			}
		}
		return counter;
	}
}