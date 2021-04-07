package com.fan.boot.modules.decmdrModule.CMDR;

import com.fan.boot.modules.decmdrModule.BasicOperation.AverageNfoldResult;
import com.fan.boot.modules.decmdrModule.BasicOperation.nfoldDataTools;

public class CMDR {
    private AverageNfoldResult averageNfoldResult = null;
    private CrossValidation crossValidation = null;

    public CMDR(nfoldDataTools nfoldData, int[] combination) {
        this.averageNfoldResult = new AverageNfoldResult(nfoldData.getnfold());
        this.crossValidation = new CrossValidation(nfoldData, combination);
    }

    public void evaluateCombination() {
        this.crossValidation.evaluateCombination();
        if (this.averageNfoldResult.getAverageErrorRate() > this.crossValidation.getAverageNfoldResult().getAverageErrorRate()) {
            this.averageNfoldResult = this.crossValidation.getAverageNfoldResult();
        }

    }

    public AverageNfoldResult getAverageNfoldResult() {
        return this.averageNfoldResult;
    }
}
