package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import com.fan.boot.modules.momdrModule.Pareto.MultiObjectiveSolution;

public class TestData extends Data{
	private MultiObjectiveSolution m_estimation = null;
	private double m_TP = 0.0d;
	private double m_FP = 0.0d;
	private double m_FN = 0.0d;
	private double m_TN = 0.0d;

	public TestData(){
		super();
	}

	public void setTP(double TP){m_TP = TP;}

	public void setFP(double FP){m_FP = FP;}

	public void setFN(double FN){m_FN = FN;}

	public void setTN(double TN){m_TN = TN;}

	public void setMultiObjectiveSolution(MultiObjectiveSolution estimation){m_estimation = estimation;}

	public double getTP(){return m_TP;}

	public double getFP(){return m_FP;}

	public double getFN(){return m_FN;}

	public double getTN(){return m_TN;}

	public MultiObjectiveSolution getMultiObjectiveSolution(){return m_estimation;}

}
