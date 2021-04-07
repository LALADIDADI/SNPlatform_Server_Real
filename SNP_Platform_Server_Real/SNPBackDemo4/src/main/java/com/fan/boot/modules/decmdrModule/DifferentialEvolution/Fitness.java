package com.fan.boot.modules.decmdrModule.DifferentialEvolution;

import com.fan.boot.modules.decmdrModule.BasicOperation.AverageNfoldResult;
import com.fan.boot.modules.decmdrModule.BasicOperation.nfoldDataTools;
import com.fan.boot.modules.decmdrModule.CMDR.CMDR;

public class Fitness {
    private AverageNfoldResult averageNfoldResult = null;

    public Fitness() {
    }

    public void computeFitness(nfoldDataTools nfold, TargetVector genome) {
        this.evaluateFitnessByCMDR(nfold, genome.getTargetVector());
    }

    public void computeFitness(nfoldDataTools nfold, int[] genome) {
        this.evaluateFitnessByCMDR(nfold, genome);
    }

    private void evaluateFitnessByCMDR(nfoldDataTools nfoldData, int[] combination) {
        CMDR rankCombination = new CMDR(nfoldData, (int[])combination.clone());
        rankCombination.evaluateCombination();
        this.averageNfoldResult = rankCombination.getAverageNfoldResult();
    }

    public AverageNfoldResult getAverageNfoldResult() {
        return this.averageNfoldResult;
    }
}
