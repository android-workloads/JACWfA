package com.intel.JACW.dmath;

import java.math.BigInteger;
import java.util.Arrays;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * <p>Test calculates an [integer] expression in its modular representation.</p>
 * Note: modules must be pairwise coprime
 * 
 *
 * @see <a href="https://en.wikipedia.org/wiki/Chinese_remainder_theorem">Chinese remainder theorem (Wikipedia)</a>
 */
public class ModularArithmeticsArrayI extends AbstractTestCase {

	@XMLParameter(defaultValue = "1000")
	public int argBase;
	
	private final int cntModules = 3;
	public final int[] mod = {13, 16, 47};
	public final int[][] modInverse = new int[cntModules][cntModules];
	public final int modMult;
	public final int[] modMultExt = new int[cntModules];
	
	private final int cntOperands = 4;
	private final int operand[] = new int[cntOperands];
	private final int operandRem[][] = new int[cntOperands][cntModules];
	private final int resRem[] = new int[cntModules];
	
	{
		int tmp = 1;
		for(int i = 0; i < cntModules; i++)
			tmp *= mod[i];
		modMult = tmp;
	}
	
	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);
		for(int i = 0; i < cntModules; i++)
			for(int j = 0; j < cntModules; j++) {
				if(i == j) continue;
				modInverse[i][j] = BigInteger.valueOf(mod[i]).modInverse(BigInteger.valueOf(mod[j])).intValue();
			}
		Arrays.fill(modMultExt, 1);
		for (int i = 0; i < cntModules; i++) {
			for (int j = 0; j < cntModules; j++) {
				if(i != j) {
					modMultExt[i] = (modMultExt[i] * mod[j] * modInverse[j][i]) % modMult;
				}
			}
		}
	}

	// dst = a * b
	private void modularMultiplication(int[] a, int[] b, int[] dst) {
		for(int i = 0; i < cntModules; i++)
			dst[i] = a[i] * b[i] % mod[i]; 
	}
	
	// dst = a - b
	private void modularSubtraction(int[] a, int[] b, int[] dst) {
		for(int i = 0; i < cntModules; i++)
			dst[i] = (a[i] - b[i] + mod[i]) % mod[i]; 
	}
	
	@Override
	public long iteration() {
		long count = 0;
		for (int it = 0; it < repeats; it++) {
			for (int opId = 0; opId < cntOperands; opId++) {
				operand[opId] = argBase + it * (opId + 1);
				for (int modId = 0; modId < cntModules; modId++)
					operandRem[opId][modId] = operand[opId] % mod[modId];
			}

			modularMultiplication(operandRem[0], operandRem[1], operandRem[0]); // a^ = a * b
			modularMultiplication(operandRem[2], operandRem[3], operandRem[2]); // c^ = c * d
			modularSubtraction(operandRem[0], operandRem[2], resRem); 			// res = a^ - c^ = a * b - c * d 
			
			int result = calcGarner(resRem);

			if(config.isValidating) {
				long error = result - (1L * operand[0] * operand[1] - 1L * operand[2] * operand[3]);
				if (error % modMult != 0) {
					setError("Validation failed");
				}
			}
			count += 1;
		}
		return count;
	}

	public int calcGarner(int[] remainders){
		int result = 0;
		int multiplier = 1;
		int[] tmp = new int[cntModules];
		for (int i = 0; i < cntModules; i++) {
			tmp[i] = remainders[i];
			for (int j = 0; j < i; j++) {
				tmp[i] = (tmp[i] - tmp[j]) * modInverse[j][i];
				tmp[i] = (tmp[i] % mod[i] + mod[i]) % mod[i];
			}
			result += multiplier * tmp[i];
			multiplier *= mod[i]; 
		}
		return result;
	}
}
