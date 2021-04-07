package com.fan.boot.modules.decmdrModule.DifferentialEvolution;

import com.fan.boot.modules.decmdrModule.BasicOperation.AverageNfoldResult;

public class Selection implements I_Selection {
    private TargetVector[] m_genome = null;
    private int[][] m_trialVector = null;
    private TargetVector[] m_targetVector = null;
    private Fitness m_fitness = null;
    private AlogrithmParameter m_parameter = null;

    public void setGenome(TargetVector[] genome) {
        this.m_genome = (TargetVector[])genome.clone();
    }

    public void setTrivalVector(int[][] trialVector) {
        this.m_trialVector = (int[][])trialVector.clone();
    }

    public TargetVector[] getTargetVector() {
        return this.m_targetVector;
    }

    public Selection(AlogrithmParameter parameter) {
        this.m_parameter = parameter;
        this.init();
    }

    private void init() {
        this.m_trialVector = new int[this.m_parameter.getSwarmSize()][this.m_parameter.getGenomeDimension()];
        this.m_targetVector = new TargetVector[this.m_parameter.getSwarmSize()];
        this.m_fitness = new Fitness();
    }

    public void createTargetVector() {
        AverageNfoldResult genomeFitness = null;
        AverageNfoldResult trialVectorFitness = null;

        for(int i = 0; i < this.m_parameter.getSwarmSize(); ++i) {
            if (this.m_genome[i].getResult() == null) {
                this.m_fitness.computeFitness(this.m_parameter.getNfoldData(), this.m_genome[i]);
                genomeFitness = this.m_fitness.getAverageNfoldResult();
            } else {
                genomeFitness = this.m_genome[i].getResult();
            }

            this.m_fitness.computeFitness(this.m_parameter.getNfoldData(), this.m_trialVector[i]);
            trialVectorFitness = this.m_fitness.getAverageNfoldResult();
            if (trialVectorFitness.getAverageErrorRate() <= genomeFitness.getAverageErrorRate()) {
                this.m_targetVector[i] = new TargetVector((int[])this.m_trialVector[i].clone(), trialVectorFitness);
            } else {
                this.m_targetVector[i] = new TargetVector((int[])this.m_genome[i].getTargetVector().clone(), genomeFitness);
            }
        }

    }
}
