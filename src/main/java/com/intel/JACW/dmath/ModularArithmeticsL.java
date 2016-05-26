package com.intel.JACW.dmath;

import java.math.BigInteger;

import com.intel.mttest.config.RunConfig;
import com.intel.mttest.exception.InvalidTestFormatException;
import com.intel.mttest.loaders.XMLParameter;
import com.intel.mttest.representation.AbstractTestCase;


/**
 * <p>Test calculates a [long] expression in its modular representation.</p>
 * Note: modules must be pairwise coprime
 * 
 *
 * @see <a href="https://en.wikipedia.org/wiki/Chinese_remainder_theorem">Chinese remainder theorem (Wikipedia)</a>
 */
public class ModularArithmeticsL extends AbstractTestCase {

	@XMLParameter(defaultValue = "1000")
	public int argBase;
	
	// cntModules = 3;
	public long mod_0 = 13, mod_1 = 16, mod_2 = 47;
	public long /*not def */   modInverse_01, modInverse_02,
				modInverse_10,  /*not def */  modInverse_12,
				modInverse_20, modInverse_21  /*not def */ ;
	
	public long modMult;
	public long modMultExt_0, modMultExt_1, modMultExt_2;
	
	// cntOperands = 4;
	private long operand_0, operand_1, operand_2, operand_3; 
	private long 	operandRem_00, operandRem_01, operandRem_02,
					operandRem_10, operandRem_11, operandRem_12,
					operandRem_20, operandRem_21, operandRem_22,
					operandRem_30, operandRem_31, operandRem_32;
	
	private long resRem_0, resRem_1, resRem_2;
	
	{
		modMult = mod_0 * mod_1 * mod_2;
	}

	@Override
	public void init(RunConfig config) throws InvalidTestFormatException {
		super.init(config);

		modInverse_01 = BigInteger.valueOf(mod_0).modInverse(BigInteger.valueOf(mod_1)).intValue();
		modInverse_02 = BigInteger.valueOf(mod_0).modInverse(BigInteger.valueOf(mod_2)).intValue();

		modInverse_10 = BigInteger.valueOf(mod_1).modInverse(BigInteger.valueOf(mod_0)).intValue();
		modInverse_12 = BigInteger.valueOf(mod_1).modInverse(BigInteger.valueOf(mod_2)).intValue();

		modInverse_20 = BigInteger.valueOf(mod_2).modInverse(BigInteger.valueOf(mod_0)).intValue();
		modInverse_21 = BigInteger.valueOf(mod_2).modInverse(BigInteger.valueOf(mod_1)).intValue();

		modMultExt_0 = mod_1 * mod_2 * modInverse_10 * modInverse_20 % modMult;
		modMultExt_1 = mod_0 * mod_2 * modInverse_01 * modInverse_21 % modMult;
		modMultExt_2 = mod_0 * mod_1 * modInverse_02 * modInverse_12 % modMult;
	}
	
	@Override
	public long iteration() {
		long count = 0;
		for (int it = 0; it < repeats; it++) {
			operand_0 = argBase + it * 1;
			operand_1 = argBase + it * 2;
			operand_2 = argBase + it * 3;
			operand_3 = argBase + it * 4;

			operandRem_00 = operand_0 % mod_0;
			operandRem_01 = operand_0 % mod_1;
			operandRem_02 = operand_0 % mod_2;

			operandRem_10 = operand_1 % mod_0;
			operandRem_11 = operand_1 % mod_1;
			operandRem_12 = operand_1 % mod_2;

			operandRem_20 = operand_2 % mod_0;
			operandRem_21 = operand_2 % mod_1;
			operandRem_22 = operand_2 % mod_2;

			operandRem_30 = operand_3 % mod_0;
			operandRem_31 = operand_3 % mod_1;
			operandRem_32 = operand_3 % mod_2;

			resRem_0 = (operandRem_00 * operandRem_10 - operandRem_20 * operandRem_30) % mod_0;
			resRem_1 = (operandRem_01 * operandRem_11 - operandRem_21 * operandRem_31) % mod_1;
			resRem_2 = (operandRem_02 * operandRem_12 - operandRem_22 * operandRem_32) % mod_2;
			long result = calcFast(resRem_0, resRem_1, resRem_2);
			
			if(config.isValidating) {
				long error = result - (operand_0 * operand_1 - operand_2 * operand_3);
				if (error % modMult != 0) {
					setError("Validation failed");
				}
			}
			count += 1;
		}
		return count;
	}

	public long calcFast(long r0, long r1, long r2) {
		return (r0 * modMultExt_0 + r1 * modMultExt_1 + r2 * modMultExt_2 ) % modMult;
	}
}
