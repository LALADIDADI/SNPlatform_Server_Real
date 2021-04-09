package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

import java.util.ArrayList;

public class DivideData {
	public DivideData(){}

	public ArrayList<Sample[]> getDivideData(Data data, int divideNumber, int seed){
		ArrayList<Sample[]> dividedData = new ArrayList<Sample[]>();
		data.RandomData(seed);
		int groupSize = (int) ((double)data.getDataSize()/divideNumber+0.5);
		System.out.println("*****************************\n " +
							"Divided dataset for " + divideNumber + "-fold CV\n" +
							"*****************************\n" +
							"Total number of samples = " +
							data.getDataSize()+ "\nAverage of group szie = " + groupSize);
		int start = 0;
		for(int i=0;i<divideNumber-1;i++){
			start = i*groupSize;
			dividedData.add(data.getRegionOfData(start, start+groupSize-1));
			System.out.println((i+1) + "-fold sub-dataset: (" + (start+1) + " - " + (start+groupSize) + ")");
		}
		dividedData.add(data.getRegionOfData(start+groupSize, data.getDataSize()-1));
		System.out.println((divideNumber) + "-fold sub-dataset: (" + (start+groupSize+1) + " - " + (data.getDataSize()) + ")");
		return dividedData;
	}
}
