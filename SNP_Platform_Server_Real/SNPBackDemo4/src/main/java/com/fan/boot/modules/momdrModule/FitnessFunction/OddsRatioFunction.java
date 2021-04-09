package com.fan.boot.modules.momdrModule.FitnessFunction;

import com.fan.boot.modules.momdrModule.Math.CIvalue;

public class OddsRatioFunction implements fitnessFunction
{

	@Override
	public double getResults(double TP, double FP, double FN, double TN)
	{
		// TODO Auto-generated method stub
		return (TP / FP) / (FN / TN);
	}

	public CIvalue getConfidenceInterval(double TP, double FP, double FN, double TN)
	{
		double or = getResults(TP, FP, FN, TN);
		double natural = Math.log(or);
		double se = Math.sqrt(1 / TP + 1 / FP + 1 / FN + 1 / TN);
		double upper = Math.exp((natural + 1.96 * se));
		double lower = Math.exp((natural - 1.96 * se));
		return new CIvalue(or, upper, lower);
	}

	@Override
	public boolean getMinOrMax()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getFunctionName() {
		// TODO Auto-generated method stub
		return "OR";
	}

	@Override
	public int getFunctionCode() {
		// TODO Auto-generated method stub
		return 6;
	}
}
