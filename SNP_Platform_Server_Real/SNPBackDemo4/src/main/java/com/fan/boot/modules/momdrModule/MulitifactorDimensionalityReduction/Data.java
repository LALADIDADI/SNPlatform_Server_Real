package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Data implements I_Data{
	/**
	 * The column 0 is defined as the case (0) or control (1).
	 * */
	private int m_DataSize = 0;
	private int m_DataDimension = 0;
	private Sample[] m_Data = null;
	private int m_countCaseNumber = 0;
	private int m_countControlNumber = 0;
	private int m_GenotypeNumber = 0;
	private String[] m_heading = null;

	public Data()
	{

	}

	public Data(ReadData readData){
		setData(readData.getReadData());
		setDataSize(readData.getDataSize());
		setDataDimension(readData.getDataDimension());
		setGenotypeNumber(readData.getGenotypeNumber());
		setCaseNumber(readData.getCaseNumber());
		setControlNumber(readData.getControlNumber());
		setHeading(readData.getHeading());
	}

	public void setData(Sample[] data){m_Data = data.clone();}

	public void setDataSize(int size){m_DataSize = size;}

	public void setDataDimension(int length){m_DataDimension = length;}

	public void setGenotypeNumber(int number){m_GenotypeNumber = number;}

	public void setCaseNumber(int n){m_countCaseNumber = n;}

	public void setControlNumber(int n){m_countControlNumber = n;}

	public void setHeading(String[] s){m_heading = s.clone();}

	public Sample[] getData(){return m_Data;}

	public int getCaseNumber(){return m_countCaseNumber;}

	public int getControlNumber(){return m_countControlNumber;}

	public String[] getHeading(){return m_heading;}

	public int getGenotypeNumber(){return m_GenotypeNumber;}

	public int getDataSize(){
		if(m_Data!=null && m_DataSize==0){
			m_DataSize = m_Data.length;
			return m_DataSize;
		}
		return m_DataSize;
	}

	public int getDataDimension(){
		if(m_Data!=null && m_DataDimension==0){
			m_DataDimension = m_Data[0].getElements().length-1;
			return m_DataDimension;
		}
		return m_DataDimension;
	}

	public Sample[] getRegionOfData(int s, int e){
		Sample[] subData = new Sample[e-s+1];
		System.arraycopy(m_Data, s, subData, 0, e-s+1);
		return subData;
	}

	public Sample getIndexOfData(int index){
		return m_Data[index];
	}

	public int getDataValue(int row, int column){
		return m_Data[row].getIndexOfElements(column);
	}

	public boolean getSampleType(int row)
	{
		return m_Data[row].getType();
	}

	public void RandomData(int seed){
		ArrayList<Sample> randomData = new ArrayList<Sample>();
		for(Sample data:m_Data){
			randomData.add(data);
		}
		Random r = new Random();
		r.setSeed(seed);
		Collections.shuffle(randomData,r);
		for(int i=0;i<randomData.size();i++){
			m_Data[i] = randomData.get(i);
		}
	}

}
