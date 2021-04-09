package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

public class Cell {
	private int[] m_SNP = null;
	private int m_TotalCase = 0;
	private int m_TotalControl = 0;
	private double m_CellValue = Double.MAX_VALUE;
	private double m_Threshold = 1;
	private int m_WholeCaseSize = 0;
	private int m_WholeControlSize = 0;

	public void setThreshold(double threshold){
		m_Threshold = threshold;
	}

	public void IncreaseCaseCount(){
		m_TotalCase++;
	}

	public void IncreaseControlCount(){
		m_TotalControl++;
	}

	public void setWholeCaseSize(int n){
		m_WholeCaseSize = n;
	}

	public void setWholeControlSize(int n){
		m_WholeControlSize = n;
	}

	public Cell(){}

	public Cell(boolean caseOrcontrol){
		if(caseOrcontrol){
			IncreaseCaseCount();
		}else {
			IncreaseControlCount();
		}
	}

	public void SetSNPs(int[] SNP){m_SNP = SNP.clone();}

	public void setTotalCase(int n){m_TotalCase = n;}

	public void setTotalControl(int n){m_TotalControl = n;}

	public void setCellValue(double n){m_CellValue = n;}

	public int[] getSNP(){return m_SNP;}

	public int getTotalCase(){return m_TotalCase;}

	public int getTotalControl(){return m_TotalControl;}

	public int getWholeCaseSize(){return m_WholeCaseSize;}

	public int getWholeControlSize(){return m_WholeControlSize;}

	public double getCellValue(){return m_CellValue;}

	public boolean getGroupType(){//true is high and false is low
		return m_CellValue >= m_Threshold ? true : false;
	}

}
