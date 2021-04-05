package com.fan.boot.modules.cmdrModule.DifferentialEvolution;

public interface I_Selection {
    void setGenome(TargetVector[] var1);

    void setTrivalVector(int[][] var1);

    TargetVector[] getTargetVector();

    void createTargetVector();
}
