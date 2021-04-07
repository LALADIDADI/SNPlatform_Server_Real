package com.fan.boot.modules.decmdrModule.DifferentialEvolution;

public interface I_Recombination {
    void setGenome(TargetVector[] var1);

    void setDonorVector(int[][] var1);

    int[][] getTrialVector();

    void createTrialVector();
}
