package com.fan.boot.modules.momdrModule.FitnessFunction;

public class CorrectClassificationFunction implements fitnessFunction
{

	@Override
	public double getResults(double TP, double FP, double FN, double TN)
	{
		// TODO Auto-generated method stub
		return 0.5*( TP / ( FP + TN ) + TN / ( TP + FN ));
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
		return "CCR";
	}

	@Override
	public int getFunctionCode() {
		// TODO Auto-generated method stub
		return 1;
	}

}
