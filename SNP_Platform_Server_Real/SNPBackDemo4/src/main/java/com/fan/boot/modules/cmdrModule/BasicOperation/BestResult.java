package com.fan.boot.modules.cmdrModule.BasicOperation;

public class BestResult {
    private AverageNfoldResult bestAverageNfoldResult = null;
    private String[] bestCombination = null;
    private int[] bestRanks = null;

    public BestResult(AverageNfoldResult averageNfoldResult, String[] combination, int[] ranks) {
        this.bestAverageNfoldResult = averageNfoldResult;
        this.bestCombination = combination;
        this.bestRanks = ranks;
    }

    public AverageNfoldResult getAverageNfoldResult() {
        return this.bestAverageNfoldResult;
    }

    public String[] getCombination() {
        return this.bestCombination;
    }

    public int[] getRanks() {
        return this.bestRanks;
    }
}
