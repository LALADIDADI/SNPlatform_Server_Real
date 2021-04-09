package com.fan.boot.modules.momdrModule.Pareto;

import java.util.Map;
import java.util.Map.Entry;

import com.fan.boot.modules.momdrModule.FitnessFunction.Fitnesses;
import com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction.MultifactorDimensionalityReduction;
import com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction.TrainingData;

public class CompareDominated
{

	private final int DOMINATE = 0;
	private final int NONDOMINATE = 1;
	private final int DOMINATED = 2;

	public CompareDominated()
	{

	}
	public int isDominated( TrainingData A , TrainingData B)
	{
		Map<Integer, Fitnesses> functions = A.getTrainingEstimation().getFitnesses();
		int fitnessSize = MultifactorDimensionalityReduction.functions.length;
		int numberOfDominated = 0;
		for(Entry<Integer, Fitnesses> entry: functions.entrySet())
		{
			if(MultifactorDimensionalityReduction.objective.get(entry.getKey()))
			{
				if(entry.getValue().getValue() < B.getTrainingEstimation().getFitnesses().get(entry.getKey()).getValue())
				{
					numberOfDominated++;
				}
			}
			else
			{
				if(entry.getValue().getValue() > B.getTrainingEstimation().getFitnesses().get(entry.getKey()).getValue())
				{
					numberOfDominated++;
				}
			}
		}

		return numberOfDominated==fitnessSize?DOMINATED:numberOfDominated==0?DOMINATE:NONDOMINATE;
	}
}
