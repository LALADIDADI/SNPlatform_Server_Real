package com.fan.boot.modules.momdrModule.Pareto;

import java.util.HashMap;
import java.util.Map;

import com.fan.boot.modules.momdrModule.FitnessFunction.Fitnesses;
import com.fan.boot.modules.momdrModule.FitnessFunction.fitnessFunction;

public class MultiObjectiveSolution
{
	private Map<Integer, Fitnesses> fitnessValues = new HashMap<Integer, Fitnesses>();
	private int[] SNPs = null;
	public MultiObjectiveSolution()
	{

	}
	public MultiObjectiveSolution(int[] SNPs)
	{
		this.SNPs = SNPs.clone();
	}

	public void setSNPs(int[] SNPs)
	{
		this.SNPs = SNPs.clone();
	}

	public int[] getSNPs()
	{
		return SNPs;
	}

	public void setFitnessFunction(fitnessFunction function, double value)
	{
		if(fitnessValues.containsKey(function.getFunctionCode()))
		{
			fitnessValues.get(function.getFunctionCode()).setValue(value);
		}
		else
		{
			fitnessValues.put(function.getFunctionCode(), new Fitnesses(function.getFunctionName(), value));
		}
	}

	public int getNumberOfFunctions()
	{
		return fitnessValues.entrySet().size();
	}

	public Map<Integer, Fitnesses> getFitnesses()
	{
		return fitnessValues;
	}
}

