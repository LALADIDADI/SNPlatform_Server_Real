package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.io.IOException;

import com.fan.boot.modules.momdrModule.Pareto.ParetoOptimalFrontSolutionsSet;

public class FindBestTrainingData {
	private ParetoOptimalFrontSolutionsSet m_ParetoSolution = null;

	public FindBestTrainingData(Data data, int SNPnumber, int seed){
		try {
			findBestTrainingData(data, SNPnumber, seed);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void findBestTrainingData(Data data, int numberOfOrder, int seed) throws IOException{
		int[] availableCombination = new int[numberOfOrder];
		for(int i=0;i<numberOfOrder;i++){
			availableCombination[i]=i;
		}
		TrainingData trainingDataCandidate = new TrainingData(data, availableCombination, seed);
		m_ParetoSolution = new ParetoOptimalFrontSolutionsSet();
		m_ParetoSolution.addTrainingData(trainingDataCandidate);

		int pos = numberOfOrder-1;
		int region = data.getDataDimension()-numberOfOrder;


		while(true){
			if(availableCombination[pos]<pos+region){
				if(pos==numberOfOrder-1){
					availableCombination[pos]++;

					TrainingData TrainingData = new TrainingData(data, availableCombination, seed);

					m_ParetoSolution.addTrainingData(TrainingData);

				}else if(availableCombination[pos+1]>=pos+region){
					availableCombination[pos+1] = ++availableCombination[pos++];
					continue;
				}
			}else if(availableCombination[pos]>=pos+region){
					pos--;
					if(pos<0){break;}
					continue;
			}
		}
	}

	public ParetoOptimalFrontSolutionsSet getBestTrainingDataModel(){
		return m_ParetoSolution;
	}

}

