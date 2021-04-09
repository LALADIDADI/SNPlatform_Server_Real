package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.util.ArrayList;

public class CombinationData {
	private Data m_combinationData = null;
	private int m_countControl = 0;
	private int m_countCase = 0;

	public CombinationData(ArrayList<Sample[]> data, int exceptIndex){
		combination(data, exceptIndex);
	}

	private void combination(ArrayList<Sample[]> data, int exceptIndex){
		int sumOfSize = 0;
		for(int i=0; i<data.size();i++){
			if(i!=exceptIndex){
				sumOfSize += data.get(i).length;
			}
		}
		Sample[] combinationData = new Sample[sumOfSize];
		int nowIndex = 0;
		int caseNumber = 0;
		int controlNumber = 0;
		for(int i=0; i<data.size();i++){
			if(i!=exceptIndex){
				for(int j=0; j<data.get(i).length;j++){
					combinationData[nowIndex]= data.get(i)[j];
					if(data.get(i)[j].getType()){ // true is cases.
						caseNumber++;
					}else { //false is controls
						controlNumber++;
					}
					nowIndex++;
				}
			}
		}
		m_combinationData = new Data();
		m_combinationData.setData(combinationData);
		m_combinationData.setCaseNumber(caseNumber);
		m_combinationData.setControlNumber(controlNumber);
		m_combinationData.setDataDimension(data.get(0)[0].getElements().length);
	}

	public Sample[] getSingleData(int index, ArrayList<Sample[]> data){
		Sample[] combinationData = new Sample[data.get(index).length];
		int nowIndex = 0;
		m_countCase = 0;
		m_countControl = 0;
		for(int j=0; j<data.get(index).length;j++){
			combinationData[nowIndex]= data.get(index)[j];
			if(data.get(index)[j].getType()){ // true is cases.
				m_countCase++;
			}else { //false is controls
				m_countControl++;
			}
			nowIndex++;
		}
		return combinationData;
	}

	public int getCountCase(){
		return m_countCase;
	}

	public int getCountControl(){
		return m_countControl;
	}

	public Data getCombinationData(){
		return m_combinationData;
	}

}

