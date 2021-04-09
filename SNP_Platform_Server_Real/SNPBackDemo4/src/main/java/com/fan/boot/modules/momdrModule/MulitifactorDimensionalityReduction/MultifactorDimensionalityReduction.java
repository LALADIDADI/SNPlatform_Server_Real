package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

import com.fan.boot.modules.momdrModule.FitnessFunction.CorrectClassificationFunction;
import com.fan.boot.modules.momdrModule.FitnessFunction.Fitnesses;
import com.fan.boot.modules.momdrModule.FitnessFunction.LikelihoodRatioFunction;
import com.fan.boot.modules.momdrModule.FitnessFunction.OddsRatioFunction;
import com.fan.boot.modules.momdrModule.FitnessFunction.PvalueFunction;
import com.fan.boot.modules.momdrModule.FitnessFunction.fitnessFunction;
import com.fan.boot.modules.momdrModule.Math.CIvalue;
import com.fan.boot.modules.momdrModule.Pareto.DefineObjective;

public class MultifactorDimensionalityReduction {

	public static fitnessFunction[] functions = new fitnessFunction[]{  new CorrectClassificationFunction(),
																		new LikelihoodRatioFunction()};
	public static Map<Integer, Boolean> objective = null;
	private final int DEFAULT_VALUE = -1;

	private int numberOfOrder = 2; //default setting
	private int numberOfCV = 5;	//default setting
	private int seed = 1; //default setting
	private String filePathway = "";
	private Data data = null;

	/**
	 * 2021.04.09
	 * By DADADIDADI
	 */
	// 新建的返回文件零
	private String resDataPath;

	public MultifactorDimensionalityReduction(int order, int cv, int seed, String filePathway, String resDataPath){


		if(order != DEFAULT_VALUE)
		{
			numberOfOrder = order;
		}
		if(cv != DEFAULT_VALUE)
		{
			numberOfCV = cv;
		}
		if(seed != DEFAULT_VALUE)
		{
			this.seed = seed;
		}
		this.filePathway = filePathway;
		this.resDataPath = resDataPath;
	}

	public void implement(){

		objective = new DefineObjective(functions).getObjective();

			System.out.println("Wellcome uses MOMDR.\n Your file name is \"" + filePathway.substring(filePathway.lastIndexOf("/")+1));

			ReadData rd = new ReadData(filePathway);
			data = new Data(rd);
			DivideData dd = new DivideData();

			final long StartTime = System.currentTimeMillis();
			final CrossValidation cv = new CrossValidation(dd.getDivideData(data, numberOfCV, seed), numberOfCV, numberOfOrder, seed);

			long EndTime = System.currentTimeMillis();
			System.out.println("Time:\t" + (double)(EndTime-StartTime)/1000);

			printSolutions(cv);

	}

	public void printSolutions(CrossValidation cv)
	{
		try {
			// 新建PrintWriter对象
			PrintWriter out = new PrintWriter(new FileOutputStream(resDataPath));

			cv.getResult().PrintParetoSet(data, out);// 输出第一部分
			ArrayList <String> optimalSolutions = cv.getResult().determineBestSolutionsInParetoSet(data, out);// 输出第二部分
			printOptimalSolutions(optimalSolutions, out);// 输出第三部分,输出完成
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printOptimalSolutions(ArrayList <String> optimalSolutions, PrintWriter out)
	{
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(3);

			out.println("\n\n\n\n********************** Optimal solutions **************************");

			for (String solution : optimalSolutions) {
				String[] ss = solution.split("\t");
				out.println("SNPs: " + ss[1] + "\tCVC = " + ss[2] + ",\tIndex: " + ss[3]);

				int[] indexSNP = changeArrayFromStrToInt(((String) ss[0].subSequence(1, ss[0].length() - 1)).split(", "));
				TrainingData TrainingData = new TrainingData(data, indexSNP, seed);

				for (Fitnesses fitness : TrainingData.getEstimation().getFitnesses().values()) {
					out.println("\t" + fitness.getFunctionName() + ": " + nf.format(fitness.getValue()) + "\t");
				}
				out.println("\n\tTP: " + TrainingData.getTP() + "\tFP: " + TrainingData.getFP() +
						"\n\tFN: " + TrainingData.getFN() + "\tTN: " + TrainingData.getTN() + "\n");
				CIvalue or = new OddsRatioFunction().getConfidenceInterval(TrainingData.getTP(), TrainingData.getFP(), TrainingData.getFN(), TrainingData.getTN());
				double pvalue = new PvalueFunction().getResults(TrainingData.getTP(), TrainingData.getFP(), TrainingData.getFN(), TrainingData.getTN());
				out.println("\n\tOdds ratio: " + nf.format(or.getOddsRatio()) +
						",   95% CI ( " + nf.format(or.getLower()) + ", " + nf.format(or.getUpper()) + " )\n\tp-value: " + String.format("%.3e\n", pvalue));
			}
			out.flush();
			out.close();
	}
	private int[] changeArrayFromStrToInt(String[] str)
	{
		int[] changedArray = new int[str.length];
		for(int i = 0 ; i < str.length ; i++)
		{
			changedArray[i] = Integer.valueOf(str[i]);
		}
		return changedArray;
	}
}
