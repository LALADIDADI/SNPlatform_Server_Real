package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

public interface I_Data {

	public void setData(Sample[] data);

	public Sample[] getData();

	public int getDataSize();

	public int getDataDimension();

	public Sample[] getRegionOfData(int s, int e);

	public Sample getIndexOfData(int index);

	public int getDataValue(int row, int column);

}
