package com.fan.boot.modules.decmdrModule.DifferentialEvolution;

import com.fan.boot.modules.decmdrModule.BasicOperation.nfoldDataTools;

public class AlogrithmParameter {
    private static int m_seed = 0;
    private static int m_swarmSize = 0;
    private static int m_genomeDimension = 0;
    private static int m_max_generation = 0;
    private double m_mutationFactor = 0.0D;
    private double m_recombinationCRfactor = 0.0D;
    private int m_maxSNPNumber = 0;
    private nfoldDataTools m_nfoldData = null;

    public AlogrithmParameter() {
        m_seed = 0;
        m_swarmSize = 0;
        m_genomeDimension = 0;
        m_max_generation = 0;
        this.m_mutationFactor = 0.0D;
        this.m_recombinationCRfactor = 0.0D;
        this.m_maxSNPNumber = 0;
        this.m_nfoldData = null;
    }

    public void setSeed(int seed) {
        m_seed = seed;
    }

    public void setSwarmSize(int size) {
        m_swarmSize = size;
    }

    public void setGenomeDimension(int dimension) {
        m_genomeDimension = dimension;
    }

    public void setMaxGeneration(int MaxGeneration) {
        m_max_generation = MaxGeneration;
    }

    public void setMutationFactor(double MutationFactor) {
        this.m_mutationFactor = MutationFactor;
    }

    public void setRecombinationCRfactor(double RecombinationCRfactor) {
        this.m_recombinationCRfactor = RecombinationCRfactor;
    }

    public void setmaxSNPNumber(int maxSNPNumber) {
        this.m_maxSNPNumber = maxSNPNumber;
    }

    public void setNfoldData(nfoldDataTools nfoldData) {
        this.m_nfoldData = nfoldData;
    }

    public int getSeed() {
        return m_seed;
    }

    public int getSwarmSize() {
        return m_swarmSize;
    }

    public int getGenomeDimension() {
        return m_genomeDimension;
    }

    public int getMaxGeneration() {
        return m_max_generation;
    }

    public double getMutationFactor() {
        return this.m_mutationFactor;
    }

    public double getRecombinationCRfactor() {
        return this.m_recombinationCRfactor;
    }

    public int getMaxSNPNumber() {
        return this.m_maxSNPNumber;
    }

    public nfoldDataTools getNfoldData() {
        return this.m_nfoldData;
    }
}
