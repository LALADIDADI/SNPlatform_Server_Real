package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.util.Map;

public class FactorCollection{
	private Map<Integer, Cell> m_Combination = null;

	private ClassifyCaseControl m_Classify = null;

	public FactorCollection(Data data, int[] indexSNP, int seed, boolean trainingORtest){
		ClassifyCaseControl(data, indexSNP, seed, trainingORtest);
	}

	private void ClassifyCaseControl(Data data, int[] indexSNP, int seed, boolean trainingORtest){
		m_Classify = new ClassifyCaseControl();
		m_Classify.Classify(data, indexSNP, seed, trainingORtest);
		m_Combination = m_Classify.getClassifyGroup();
	}

	public Map<Integer, Cell> getFactorCollection(){
		return m_Combination;
	}

}

