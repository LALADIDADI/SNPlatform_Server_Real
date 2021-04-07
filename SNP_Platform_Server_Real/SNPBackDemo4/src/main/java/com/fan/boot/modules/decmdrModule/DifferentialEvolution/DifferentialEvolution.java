package com.fan.boot.modules.decmdrModule.DifferentialEvolution;

public class DifferentialEvolution {
    private int[][] m_trialVector = null;
    private int[][] m_donorVector = null;
    private TargetVector[] m_targetVector = null;
    private Mutation m_mutation = null;
    private Recombination m_recombination = null;
    private Selection m_selection = null;
    AlogrithmParameter m_parameter = null;

    public DifferentialEvolution(String DataPath) {
    }

    public DifferentialEvolution(AlogrithmParameter parameter) {
        this.m_parameter = parameter;
        this.m_mutation = new Mutation(this.m_parameter);
        this.m_recombination = new Recombination(this.m_parameter);
        this.m_selection = new Selection(this.m_parameter);
    }

    public TargetVector[] implementDE() {
        this.initialisation();
        int var1 = 1;

        while(this.termination(var1++)) {
            this.mutation();
            this.recombination();
            this.selection();
        }

        return this.m_targetVector;
    }

    private void initialisation() {
        Initialization initialisation = new Initialization(this.m_parameter);
        this.m_targetVector = (TargetVector[])initialisation.initailGenome().clone();
        initialisation = null;
        System.gc();
    }

    private void mutation() {
        this.m_mutation.setGenome(this.m_targetVector);
        this.m_mutation.createDonorVector();
        this.m_donorVector = (int[][])this.m_mutation.getDonorVector().clone();
    }

    private void recombination() {
        this.m_recombination.setDonorVector(this.m_donorVector);
        this.m_recombination.setGenome(this.m_targetVector);
        this.m_recombination.createTrialVector();
        this.m_trialVector = (int[][])this.m_recombination.getTrialVector().clone();
    }

    private void selection() {
        this.m_selection.setGenome(this.m_targetVector);
        this.m_selection.setTrivalVector(this.m_trialVector);
        this.m_selection.createTargetVector();
        this.m_targetVector = (TargetVector[])this.m_selection.getTargetVector().clone();
    }

    private boolean termination(int generation) {
        return generation != this.m_parameter.getMaxGeneration();
    }
}
