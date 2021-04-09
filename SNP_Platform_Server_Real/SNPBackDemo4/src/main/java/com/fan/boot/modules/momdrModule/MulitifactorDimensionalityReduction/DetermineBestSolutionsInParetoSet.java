package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fan.boot.modules.momdrModule.FitnessFunction.Fitnesses;
import com.fan.boot.modules.momdrModule.FitnessFunction.OddsRatioFunction;
import com.fan.boot.modules.momdrModule.FitnessFunction.PvalueFunction;
import com.fan.boot.modules.momdrModule.Math.CIvalue;
import com.fan.boot.modules.momdrModule.Pareto.ParetoOptimalFrontSolutionsSet;

public class DetermineBestSolutionsInParetoSet {
	private ArrayList<ParetoOptimalFrontSolutionsSet> m_ParetoOptimalSolutionsInCV = null;

	public DetermineBestSolutionsInParetoSet(){
		m_ParetoOptimalSolutionsInCV = new ArrayList<ParetoOptimalFrontSolutionsSet>();
	}

	public void add(ParetoOptimalFrontSolutionsSet paretoTrainingData){
		m_ParetoOptimalSolutionsInCV.add(paretoTrainingData);
	}

	public int getTrainingDateNumber(){
		return m_ParetoOptimalSolutionsInCV.size();
	}

	public ArrayList <String> determineBestSolutionsInParetoSet(Data data, PrintWriter out) throws FileNotFoundException {
		Map<Integer, solution> countSolution = new HashMap<Integer, solution>();
		for(int i = 0 ; i < m_ParetoOptimalSolutionsInCV.size() ; i++)
		{
			int paretoSize = m_ParetoOptimalSolutionsInCV.get(i).getParetoOptimaTrainingData().size();
			for(int j = 0 ; j < paretoSize ; j++)
			{
				TrainingData trainingData = m_ParetoOptimalSolutionsInCV.get(i).getParetoOptimaTrainingData().get(j);
				int keys = 0;
				int ss = 1;
				for(int k=0 ; k < trainingData.getIndexSNP().length ; k++)
				{
					keys += trainingData.getIndexSNP()[k]*ss;
					ss *= 10;
				}

				if(countSolution.containsKey(keys))
				{
					countSolution.get(keys).increaseNumber(i+1);
				}
				else
				{
					countSolution.put(keys, new solution(trainingData.getIndexSNP(), i+1));
				}
			}
		}

		out.println("********************** Summary of Pareto sets in k-fold CV **************************");
		StringBuffer sb = new StringBuffer();
		for(solution s:countSolution.values())
		{
			out.println("Candidate: " + getHeading(s.getSNPs(), data) + "\tCVC = " + s.getNumber() + ",\tIndex: " + Arrays.toString(s.getIndex()));
			sb.append(Arrays.toString(s.getSNPs()) + "\t" + getHeading(s.getSNPs(), data) + "\t" + s.getNumber() + "\t" + Arrays.toString(s.getIndex()) + "\n");
		}
		return getBestSolutions(sb);
	}

	public ArrayList<String> getBestSolutions(StringBuffer whole)
	{
		Map<Integer, ArrayList<String>> bestSolutions = new HashMap<Integer, ArrayList<String>>();
		String[] lines = whole.toString().split("\n");
		int maxNumber = 0;
		for(String contents:lines)
		{
			if(contents.contains("\t")!=true) continue;
			String[] content = contents.split("\t");
			int number = Integer.valueOf(content[2]);
			maxNumber = number>maxNumber ? number:maxNumber;
			if(bestSolutions.containsKey(number))
			{
				bestSolutions.get(number).add(contents);
			}else
			{
				ArrayList<String> cont = new ArrayList<String>();
				cont.add(contents);
				bestSolutions.put(number, cont);
			}
		}

		return bestSolutions.get(maxNumber);
	}

	public void PrintParetoSet(Data data, PrintWriter out){

		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);

		for(int i = 0 ; i < m_ParetoOptimalSolutionsInCV.size() ; i++)
		{
			out.println("************************** The " + (i+1) + "-fold **************************");
			int paretoSize = m_ParetoOptimalSolutionsInCV.get(i).getParetoOptimaTrainingData().size();
			out.println("Find " + paretoSize + (paretoSize == 1 ? " candidate" : " candidates"));
			for(int j = 0 ; j < paretoSize ; j++)
			{
				out.print("The " + (j+1) + "-th candidate:");
				TrainingData trainingData = m_ParetoOptimalSolutionsInCV.get(i).getParetoOptimaTrainingData().get(j);
				out.println("SNPs " + getHeading(trainingData.getIndexSNP(), data));
				out.println("Training data:");

				double TP = trainingData.getTP();
				double FP = trainingData.getFP();
				double FN = trainingData.getFN();
				double TN = trainingData.getTN();
				out.println("\n\tTP: " + TP + "\tFP: " + FP + "\n\tFN: " + FN + "\tTN: " + TN + "\n");
				for(Fitnesses fitness:trainingData.getEstimation().getFitnesses().values())
				{
					out.println("\t" + fitness.getFunctionName() + ": " + nf.format(fitness.getValue()) + "\t");
				}
				CIvalue or = new OddsRatioFunction().getConfidenceInterval(TP, FP, FN, TN);
				double pvalue = new PvalueFunction().getResults(TP, FP, FN, TN);
				out.println("\n\tOdds ratio: " + nf.format(or.getOddsRatio()) +
									",   95% CI ( " + nf.format(or.getLower()) + ", " + nf.format(or.getUpper()) + " )\n\tp-value: " + String.format("%.3e\n", pvalue));

				//-------------test data----------------
				out.println("Test data:");
				TestData testData = m_ParetoOptimalSolutionsInCV.get(i).getParetoOptimaTestData().get(j);

				TP = testData.getTP();
				FP = testData.getFP();
				FN = testData.getFN();
				TN = testData.getTN();
				out.println("\n\tTP: " + TP + "\tFP: " + FP + "\n\tFN: " + FN + "\tTN: " + TN + "\n");
				for(Fitnesses fitness:testData.getMultiObjectiveSolution().getFitnesses().values())
				{
					out.println("\t" + fitness.getFunctionName() + ": " + nf.format(fitness.getValue()) + "\t");
				}
				or = new OddsRatioFunction().getConfidenceInterval(TP, FP, FN, TN);
				pvalue = new PvalueFunction().getResults(TP, FP, FN, TN);
				out.println("\n\tOdds ratio: " + nf.format(or.getOddsRatio()) +
									",   95% CI ( " + nf.format(or.getLower()) + ", " + nf.format(or.getUpper()) + " )\n\tp-value: " + String.format("%.3e\n", pvalue));

			}
			out.println("\n\n\n");
		}
	}
	private String getHeading(int[] indexOfSNP, Data data)
	{
		if(data.getHeading()==null)
		{
			return Arrays.toString(indexOfSNP);
		}
		else{
			String SNPs = "";
			for(int index = 0 ; index < indexOfSNP.length-1; index++)
			{
				SNPs += data.getHeading()[indexOfSNP[index]] + ",";
			}
			SNPs += data.getHeading()[indexOfSNP[indexOfSNP.length-1]];
			return SNPs;
		}
	}
}

class solution
{
	private int number = 1;
	int[] SNPs = null;
	ArrayList<Integer> indexOfCV = new ArrayList<Integer>();
	public solution(int[] SNPs, int index)
	{
		this.SNPs = SNPs.clone();
		indexOfCV.add(index);
	}

	public void increaseNumber(int index)
	{
		number++;
		indexOfCV.add(index);
	}

	public int getNumber()
	{
		return number;
	}

	public int[] getSNPs()
	{
		return SNPs;
	}

	public Integer[] getIndex()
	{
		return indexOfCV.toArray(new Integer[indexOfCV.size()]);
	}

}

