package com.fan.boot.modules.decmdrModule.DifferentialEvolution;

public interface I_Mutation {
    void setGenome(TargetVector[] var1);

    int[][] getDonorVector();

    void createFourVectors();

    void createDonorVector();
}
