package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ReadData{
	private Sample[] m_Data;
	private int m_data_dimension = 0;
	private int m_data_number = 0;
	private int m_data_Genotype_number = 0;
	private String m_fileName = "";
	private int m_data_caseNumber = 0;
	private int m_data_controlNumber = 0;
	private String[] m_heading = null;

	public ReadData(String fileName){
		setFileName(fileName);
	};

	public void setFileName(String fileName){
		m_fileName = fileName;
		read();
	}

	public void read(){
		ArrayList<String[]> data_transfered = new ArrayList<String[]>();
		try {
			FileReader fr = new FileReader(m_fileName);
			BufferedReader bfr = new BufferedReader(fr);
			String strLine = "";
			while((strLine=bfr.readLine())!=null){
				data_transfered.add(strLine.split("\t"));
				if(strLine.charAt(strLine.length()-1)=='1'){
					m_data_caseNumber++;
				}else if(strLine.charAt(strLine.length()-1)=='0'){
					m_data_controlNumber++;
				}
			}
			bfr.close();
			fr.close();
		} catch(IOException e) {
			System.err.println(e);
		}

		m_heading = data_transfered.get(0).clone();
		data_transfered.remove(0);
		m_data_number = data_transfered.size();
		m_data_dimension = data_transfered.get(0).length-1;
		m_Data = new Sample[m_data_number];

		for(int i=0;i<m_Data.length;i++){
			int[] elements = new int[m_data_dimension];
			for(int j=0;j<m_data_dimension;j++){
				elements[j]=Integer.parseInt(data_transfered.get(i)[j]);
				m_data_Genotype_number = m_data_Genotype_number<elements[j]?
											elements[j]:m_data_Genotype_number;
			}
			m_Data[i] = new Sample(elements, Integer.parseInt(data_transfered.get(i)[data_transfered.get(i).length-1]));
		}
		m_data_Genotype_number--;
	}

	public Sample[] getReadData(){
		return m_Data;
	}

	public int getDataSize(){return m_data_number;}

	public int getDataDimension(){return m_data_dimension;}

	public int getGenotypeNumber(){return m_data_Genotype_number;}

	public int getCaseNumber(){return m_data_caseNumber;}

	public int getControlNumber(){return m_data_controlNumber;}

	public String[] getHeading(){return m_heading;}
}
