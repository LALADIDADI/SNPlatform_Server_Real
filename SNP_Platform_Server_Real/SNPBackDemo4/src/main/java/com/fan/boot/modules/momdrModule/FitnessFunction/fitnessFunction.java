package com.fan.boot.modules.momdrModule.FitnessFunction;

public interface fitnessFunction
{
	public double getResults(double TP, double FP, double FN, double TN);

	/**
	 * get fitness function belongs to maximum or minimum.
	 * false: minimum
	 * true: maximum
	 * */
	public boolean getMinOrMax();

	public String getFunctionName();

	public int getFunctionCode();
}
