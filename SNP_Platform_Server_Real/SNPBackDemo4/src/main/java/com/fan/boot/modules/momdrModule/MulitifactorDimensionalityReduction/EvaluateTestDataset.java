package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.util.Map;
import java.util.Map.Entry;

import com.fan.boot.modules.momdrModule.FitnessFunction.fitnessFunction;
import com.fan.boot.modules.momdrModule.Pareto.MultiObjectiveSolution;
import com.fan.boot.modules.momdrModule.Pareto.ParetoOptimalFrontSolutionsSet;

public class EvaluateTestDataset {

	public EvaluateTestDataset(){}

	public void evaluate(TestData testData, ParetoOptimalFrontSolutionsSet trainingDataCandidate, int seed){
		for(TrainingData trainingData:trainingDataCandidate.getParetoOptimaTrainingData())
		{
			TestData testDatas = new TestData();
			Map<Integer, Cell> trainingDataCell 	= trainingData.getFactorCollection();
			ClassifyCaseControl classify 		= new ClassifyCaseControl();
			classify.Classify(testData, trainingData.getIndexSNP(), seed, false);
			Map<Integer, Cell> testDataCell 		= classify.getClassifyGroup();

			double TP = 0, FN = 0, FP = 0, TN = 0;

			for(Entry<Integer, Cell> test_cell:testDataCell.entrySet()){
				if(trainingDataCell.containsKey(test_cell.getKey()))
				{
					if(trainingDataCell.get(test_cell.getKey()).getGroupType()){
							TP += test_cell.getValue().getTotalCase();
							FP += test_cell.getValue().getTotalControl();
					}else {
							TN += test_cell.getValue().getTotalControl();
							FN += test_cell.getValue().getTotalCase();
					}
				}
			}
			testDatas.setTP(TP);
			testDatas.setFP(FP);
			testDatas.setTN(TN);
			testDatas.setFN(FN);

			MultiObjectiveSolution estimation = new MultiObjectiveSolution();
			for(fitnessFunction functions:MultifactorDimensionalityReduction.functions)
			{
				estimation.setFitnessFunction(functions, functions.getResults(TP, FP, FN, TN));
			}
			testDatas.setMultiObjectiveSolution(estimation);
			trainingDataCandidate.setParetoTestingData(testDatas);
		}
	}
}

