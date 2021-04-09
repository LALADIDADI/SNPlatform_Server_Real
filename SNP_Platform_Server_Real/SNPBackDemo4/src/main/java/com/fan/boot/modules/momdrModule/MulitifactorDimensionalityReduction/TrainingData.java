package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.util.Map;

import com.fan.boot.modules.momdrModule.FitnessFunction.fitnessFunction;
import com.fan.boot.modules.momdrModule.Pareto.MultiObjectiveSolution;

public class TrainingData extends FactorCollection{
	private MultiObjectiveSolution m_estimation = null;
	private int[] m_indexSNP = null;
	private double m_TP = 0.0d;
	private double m_FP = 0.0d;
	private double m_FN = 0.0d;
	private double m_TN = 0.0d;
	private double m_High_Number = 0.0d;
	private double m_Low_Number = 0.0d;

	public TrainingData(Data data, int[] indexSNP, int seed){

		super(data,indexSNP, seed, true);
		m_indexSNP = indexSNP.clone();
	}

	public Map<Integer, Cell> getTrainingData(){
		return super.getFactorCollection();
	}

	public int[] getIndexSNP(){
		return m_indexSNP;
	}

	public MultiObjectiveSolution getTrainingEstimation(){
			m_estimation = new MultiObjectiveSolution(getIndexSNP());
			m_TP = 0;
			m_FP = 0;
			m_FN = 0;
			m_TN = 0;
			m_High_Number = 0.0d;
			m_Low_Number = 0.0d;
			for(Cell cell:super.getFactorCollection().values()){
				if(cell.getGroupType()){
					m_TP += cell.getTotalCase();
					m_FP += cell.getTotalControl();
					m_High_Number++;
				}else {
					m_TN += cell.getTotalControl();
					m_FN += cell.getTotalCase();
					m_Low_Number++;
				}
			}

			for(fitnessFunction functions:MultifactorDimensionalityReduction.functions)
			{
				m_estimation.setFitnessFunction(functions, functions.getResults(m_TP, m_FP, m_FN, m_TN));
			}
			return m_estimation;
	}

	public MultiObjectiveSolution getEstimation(){
		if(m_estimation==null){
			getTrainingEstimation();
		}
		return m_estimation;
	}

	public double getTP(){return m_TP;}

	public double getFP(){return m_FP;}

	public double getFN(){return m_FN;}

	public double getTN(){return m_TN;}

	public double getHighNumber(){return m_High_Number;}

	public double getLowNumber(){return m_Low_Number;}

}
