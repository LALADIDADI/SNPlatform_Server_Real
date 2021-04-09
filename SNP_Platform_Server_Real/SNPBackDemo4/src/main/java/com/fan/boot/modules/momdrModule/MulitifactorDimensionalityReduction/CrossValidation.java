package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.util.ArrayList;

import com.fan.boot.modules.momdrModule.Pareto.ParetoOptimalFrontSolutionsSet;


public class CrossValidation {
	private DetermineBestSolutionsInParetoSet m_Result = null;

	public CrossValidation(ArrayList<Sample[]> data, int nfold, int SNPnumber, int seed){
		m_Result = new DetermineBestSolutionsInParetoSet();
		System.out.println("*****************************\nMOMDR operation\n*****************************");
		crossValidation(data, nfold, SNPnumber, seed);
	}

	private void crossValidation(final ArrayList<Sample[]> data, int numberOfCV, final int numberOfOrder, final int seed){
		final EvaluateTestDataset cc = new EvaluateTestDataset();
		for(int i=0;i<numberOfCV;i++){
			final int j = i;
			CombinationData divideData = new CombinationData(data, j);
			ParetoOptimalFrontSolutionsSet bestParetoTrainingDataModel = new FindBestTrainingData(divideData.getCombinationData(), numberOfOrder, seed).getBestTrainingDataModel();

			TestData testData = new TestData();
			testData.setData(divideData.getSingleData(j, data));
			testData.setCaseNumber(divideData.getCountCase());
			testData.setControlNumber(divideData.getCountControl());
			cc.evaluate(testData,bestParetoTrainingDataModel, seed);
			m_Result.add(bestParetoTrainingDataModel);
		}
	}

	public DetermineBestSolutionsInParetoSet getResult(){
		return m_Result;
	}

}

