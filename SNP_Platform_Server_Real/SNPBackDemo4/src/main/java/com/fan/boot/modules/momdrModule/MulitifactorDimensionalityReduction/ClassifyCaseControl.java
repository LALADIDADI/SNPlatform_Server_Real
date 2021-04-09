package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.util.HashMap;
import java.util.Map;


public class ClassifyCaseControl {
	private Map<Integer, Cell> m_ClassifyGroup = new HashMap<Integer, Cell>();

	public ClassifyCaseControl(){}

	public void Classify(Data data, int[] indexSNP, int seed, boolean trainingORtest){
		//if trainingORtest = true, it represent the training
		//if trainingORtest = false, it represent the test
		for(int i=0;i<data.getDataSize();i++){
			boolean caseOrcontrol = data.getSampleType(i);
			int genotype = 0;
			int ss = 1;
			for(int j=0;j<indexSNP.length;j++){
				genotype += data.getData()[i].getIndexOfElements(indexSNP[j])*ss;
				ss*=10;
			}
			if(m_ClassifyGroup.containsKey(genotype))
			{
				if(caseOrcontrol)
				{
					m_ClassifyGroup.get(genotype).IncreaseCaseCount();
				}
				else
				{
					m_ClassifyGroup.get(genotype).IncreaseControlCount();
				}
			}
			else
			{
				Cell group = new Cell(caseOrcontrol);
				m_ClassifyGroup.put(genotype, group);
			}
		}

		for(Cell cell:m_ClassifyGroup.values()){
			cell.setCellValue
			(
				new CalculateCell().Calculate
				(
					cell, 	data.getCaseNumber(), 	data.getControlNumber()
				)
			);
		}
	}

	public Map<Integer, Cell> getClassifyGroup(){
		return m_ClassifyGroup;
	}

}

