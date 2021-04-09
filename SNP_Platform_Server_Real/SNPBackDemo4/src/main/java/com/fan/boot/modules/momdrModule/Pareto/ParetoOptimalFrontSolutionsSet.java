package com.fan.boot.modules.momdrModule.Pareto;

import java.util.ArrayList;

import com.fan.boot.modules.momdrModule.FitnessFunction.Fitnesses;
import com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction.TestData;
import com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction.TrainingData;

public class ParetoOptimalFrontSolutionsSet
{
	private ArrayList<TrainingData> paretoOptimalTrainingData = null;
	private ArrayList<TestData> paretoTesting = null;
	private final int DOMINATE = 0;
	private final int DOMINATED = 2;
	private CompareDominated compare = new CompareDominated();

	public ParetoOptimalFrontSolutionsSet()
	{
		paretoOptimalTrainingData = new ArrayList<TrainingData>();
		paretoTesting = new ArrayList<TestData>();
	}

	public void addTrainingData( TrainingData solution )
	{
		if( paretoOptimalTrainingData.size() != 0 )
		{
			boolean flag = true;
			ArrayList<Integer> index = new ArrayList<Integer>();
			for( int i = 0 ; i < paretoOptimalTrainingData.size() ; i++ )
			{
				int value = isDominated( solution , paretoOptimalTrainingData.get( i ) );

				if(value == DOMINATED)
				{
					flag = false;
					break;
				}
				else if(value == DOMINATE)
				{
					index.add( i );
				}
			}
			if ( flag )
			{
				for( int i = index.size()-1 ; i >= 0 ; i-- )
				{
					paretoOptimalTrainingData.remove( index.get( i ).intValue() );
				}
				paretoOptimalTrainingData.add( solution );
			}
		}
		else if( paretoOptimalTrainingData.size() == 0 )
		{
			paretoOptimalTrainingData.add( solution );
		}
	}

	private int isDominated( TrainingData v1 , TrainingData v2 )
	{
		return compare.isDominated( v1 , v2 );
	}

	public TrainingData getParetoOptimaTrainingData(int index)
	{
		return paretoOptimalTrainingData.get(index);
	}

	public ArrayList<TrainingData> getParetoOptimaTrainingData()
	{
		return paretoOptimalTrainingData;
	}

	public void setParetoTestingData(TestData testData)
	{
		paretoTesting.add(testData);
	}

	public ArrayList<TestData> getParetoOptimaTestData()
	{
		return paretoTesting;
	}

	public void showPareto()
	{
		for( int i = 0 ; i < paretoOptimalTrainingData.size() ; i++ )
		{
			for(Fitnesses fitness:paretoOptimalTrainingData.get(i).getTrainingEstimation().getFitnesses().values())
			{
				System.out.print(fitness.getFunctionName() + ":\t" + fitness.getValue() + "\t");
			}
		}
	}
}

