package com.fan.boot.modules.momdrModule.FitnessFunction;

public class LikelihoodRatioFunction implements fitnessFunction
{

	@Override
	public double getResults(double TP, double FP, double FN, double TN) {
		// TODO Auto-generated method stub
		double n1 = TP + FN;
		double n2 = FP + TN;
		double m1 = TP + FP;
		double m2 = FN + TN;
		double nm = n1 + n2;
		double eTP = (n1 * m1) / nm;
		double eFP = (n2 * m1) / nm;
		double eFN = (n1 * m2) / nm;
		double eTN = (n2 * m2) / nm;
		return 2 * ((TP * getLog(TP, eTP)) +
					(FP * getLog(FP, eFP)) +
					(FN * getLog(FN, eFN)) +
					(TN * getLog(TN, eTN))
					);
	}

	private double getLog(double value, double value2)
	{
		if(value == 0 || value2 ==0)
		{
			return 0;
		}else
		{
			return Math.log(value / value2);
		}

	}

	@Override
	public boolean getMinOrMax() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getFunctionName() {
		// TODO Auto-generated method stub
		return "LR";
	}

	@Override
	public int getFunctionCode() {
		// TODO Auto-generated method stub
		return 14;
	}
}
