package com.fan.boot.modules.momdrModule.Pareto;

import java.util.HashMap;
import java.util.Map;

import com.fan.boot.modules.momdrModule.FitnessFunction.fitnessFunction;

public class DefineObjective
{
	private fitnessFunction[] fitnessFunctions = null;
	Map<Integer, Boolean> objective = null;

	public DefineObjective(fitnessFunction[] fitnessFunctions)
	{
		this.fitnessFunctions = fitnessFunctions.clone();
	}

	public Map<Integer, Boolean> getObjective()
	{
		objective = new HashMap<Integer, Boolean>();
		for(fitnessFunction function:fitnessFunctions)
		{
			objective.put(function.getFunctionCode(), function.getMinOrMax());
		}
		return objective;
	}
}

