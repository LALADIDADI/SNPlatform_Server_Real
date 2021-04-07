package com.fan.boot.modules.decmdrModule.CMDR;

import com.fan.boot.modules.decmdrModule.BasicOperation.AverageNfoldResult;
import com.fan.boot.modules.decmdrModule.BasicOperation.CategoryColumnIncludeCells;
import com.fan.boot.modules.decmdrModule.BasicOperation.nfoldDataTools;
import com.fan.boot.modules.decmdrModule.Data.IndependentAndDependentVariables;
import com.fan.boot.modules.decmdrModule.Data.ColumnInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CrossValidation {
    private AverageNfoldResult averageNfoldResult = null;
    private nfoldDataTools nfoldDatas = null;
    private int[] permutationCombination = null;

    public CrossValidation(nfoldDataTools nfoldData, int[] permutationCombination) {
        this.nfoldDatas = nfoldData;
        this.permutationCombination = permutationCombination;
    }

    public void evaluateCombination() {
        Map<String, IndependentAndDependentVariables> dataset = this.nfoldDatas.getColumnData();
        double totalErrorRate = 0.0D;
        this.averageNfoldResult = new AverageNfoldResult(this.nfoldDatas.getnfold());
        this.averageNfoldResult.setCombination((int[])this.permutationCombination.clone());

        for(int testing = 1; testing <= this.nfoldDatas.getnfold(); ++testing) {
            Map<String, MDRCell> MDRCells = new HashMap();

            for(int training = 1; training <= this.nfoldDatas.getnfold(); ++training) {
                if (testing != training) {
                    ColumnInformation dependent = ((IndependentAndDependentVariables)dataset.get(String.valueOf(training))).getDependentVariable();
                    ArrayList<ColumnInformation<?>> independent = ((IndependentAndDependentVariables)dataset.get(String.valueOf(training))).getIndependentVariable();

                    for(int sample_index = 0; sample_index < dependent.getLength(); ++sample_index) {
                        StringBuffer MDRCellCode = new StringBuffer();

                        for(int column_index = 0; column_index < this.permutationCombination.length; ++column_index) {
                            MDRCellCode.append(((CategoryColumnIncludeCells)independent.get(this.permutationCombination[column_index])).getValue(sample_index));
                        }

                        if (MDRCells.containsKey(MDRCellCode.toString())) {
                            ((MDRCell)MDRCells.get(MDRCellCode.toString())).increaseNumberOfCaseOrControl(((CategoryColumnIncludeCells)dependent).getBooleanValue(sample_index));
                        } else {
                            MDRCells.put(MDRCellCode.toString(), new MDRCell(((CategoryColumnIncludeCells)dependent).getBooleanValue(sample_index), 200, 200));
                        }
                    }
                }
            }

            double TP = 0.0D;
            double FP = 0.0D;
            double TN = 0.0D;
            double FN = 0.0D;
            ColumnInformation<?> dependent = ((IndependentAndDependentVariables)dataset.get(String.valueOf(testing))).getDependentVariable();
            ArrayList<ColumnInformation<?>> independent = ((IndependentAndDependentVariables)dataset.get(String.valueOf(testing))).getIndependentVariable();

            for(int sample_index = 0; sample_index < dependent.getLength(); ++sample_index) {
                StringBuffer MDRCellCode = new StringBuffer();

                for(int column_index = 0; column_index < this.permutationCombination.length; ++column_index) {
                    MDRCellCode.append(((CategoryColumnIncludeCells)independent.get(this.permutationCombination[column_index])).getValue(sample_index));
                }

                if (MDRCells.containsKey(MDRCellCode.toString())) {
                    if (((MDRCell)MDRCells.get(MDRCellCode.toString())).getCaseOrControl()) {
                        if (((CategoryColumnIncludeCells)dependent).getBooleanValue(sample_index)) {
                            ++TP;
                        } else {
                            ++FP;
                        }
                    } else if (((CategoryColumnIncludeCells)dependent).getBooleanValue(sample_index)) {
                        ++FN;
                    } else {
                        ++TN;
                    }
                }
            }

            totalErrorRate = 0.5D * (FP / (FP + TN) + FN / (TP + FN));
            this.averageNfoldResult.setTP_FP_TN_FN(TP, FP, TN, FN, testing - 1);
            this.averageNfoldResult.setErrorRate(testing - 1, totalErrorRate);
        }

        this.averageNfoldResult.computeAverageErrorRate();
    }

    public AverageNfoldResult getAverageNfoldResult() {
        return this.averageNfoldResult;
    }
}
