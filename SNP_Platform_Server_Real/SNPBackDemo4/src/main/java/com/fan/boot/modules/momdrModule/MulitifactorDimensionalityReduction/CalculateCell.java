package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

public class CalculateCell {
	public CalculateCell(){}

	public double Calculate(Cell cell, int countCase, int countControl){
		if(cell.getTotalControl()==0){
			if(cell.getTotalCase()!=0){
				return 1;
			}
			return 0;
		}
		return
			( (double)cell.getTotalCase() / countCase )/
			( (double)cell.getTotalControl() / countControl );
	}
}

