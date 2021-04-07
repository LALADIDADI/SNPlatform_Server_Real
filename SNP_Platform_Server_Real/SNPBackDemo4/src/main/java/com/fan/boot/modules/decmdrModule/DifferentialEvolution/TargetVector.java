package com.fan.boot.modules.decmdrModule.DifferentialEvolution;

import com.fan.boot.modules.decmdrModule.BasicOperation.AverageNfoldResult;

public class TargetVector {
    public int[] m_targetVector = null;
    public AverageNfoldResult m_result = null;

    public TargetVector() {
    }

    public TargetVector(int[] targetVector, AverageNfoldResult result) {
        this.m_targetVector = (int[])targetVector.clone();
        this.m_result = result;
    }

    public void setTargetVector(int[] targetVector) {
        this.m_targetVector = (int[])targetVector.clone();
    }

    public void setTargetVectorResult(AverageNfoldResult result) {
        this.m_result = result;
    }

    public int[] getTargetVector() {
        return (int[])this.m_targetVector.clone();
    }

    public AverageNfoldResult getResult() {
        return this.m_result;
    }
}
