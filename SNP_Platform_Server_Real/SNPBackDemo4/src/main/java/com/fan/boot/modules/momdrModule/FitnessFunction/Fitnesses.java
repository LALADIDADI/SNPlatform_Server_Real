package com.fan.boot.modules.momdrModule.FitnessFunction;

public class Fitnesses
{
	private String functionName = "";
	private double value = 0.0d;

	public Fitnesses(String functionName, double value)
	{
		this.functionName = functionName;
		this.value = value;
	}

	public void setValue(double value)
	{
		this.value = value;
	}

	public String getFunctionName()
	{
		return functionName;
	}

	public double getValue()
	{
		return value;
	}
}
