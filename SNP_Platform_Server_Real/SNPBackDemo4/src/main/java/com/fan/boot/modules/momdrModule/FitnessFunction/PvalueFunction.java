package com.fan.boot.modules.momdrModule.FitnessFunction;

import java.math.BigDecimal;

import com.fan.boot.modules.momdrModule.Math.Combination;

public class PvalueFunction implements fitnessFunction
{

	@Override
	public double getResults(double TP, double FP, double FN, double TN)
	{
		// TODO Auto-generated method stub
		double X =TP;
		double r1 = TP + FP;
		double r2 = FN + TN;
		double d1 = TP + FN;
		double t = TP + FP + FN + TN;
		BigDecimal p3 = new Combination().C((int)t, (int)d1);
		BigDecimal value = new BigDecimal("0");
		for(int i = (int)X ; i <= r1 ; i++)
		{
			BigDecimal p1 = new Combination().C((int)r1, i);
			BigDecimal p2 = new Combination().C((int)r2, (int)d1-i);
			BigDecimal pp = p1.multiply(p2);
			pp = pp.divide(p3, 100, BigDecimal.ROUND_DOWN);
			value = value.add(pp);
		}

		return value.doubleValue();
	}

	@Override
	public boolean getMinOrMax()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getFunctionName() {
		// TODO Auto-generated method stub
		return "Pvalue";
	}

	@Override
	public int getFunctionCode() {
		// TODO Auto-generated method stub
		return 9;
	}

}
