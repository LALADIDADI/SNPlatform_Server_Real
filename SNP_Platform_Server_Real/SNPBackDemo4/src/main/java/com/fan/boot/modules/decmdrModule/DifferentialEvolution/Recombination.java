package com.fan.boot.modules.decmdrModule.DifferentialEvolution;

import java.util.Random;

public class Recombination implements I_Recombination {
    private TargetVector[] m_genome = null;
    private int[][] m_donorVector = null;
    private int[][] m_trialVector = null;
    private Random m_rand = null;
    private AlogrithmParameter m_parameter = null;

    public void setGenome(TargetVector[] genome) {
        this.m_genome = (TargetVector[])genome.clone();
    }

    public void setDonorVector(int[][] donorVector) {
        this.m_donorVector = (int[][])donorVector.clone();
    }

    public int[][] getTrialVector() {
        return this.m_trialVector;
    }

    public Recombination(AlogrithmParameter parameter) {
        this.m_parameter = parameter;
        this.init();
    }

    private void init() {
        this.m_trialVector = new int[this.m_parameter.getSwarmSize()][this.m_parameter.getGenomeDimension()];
        this.m_rand = new Random();
        this.m_rand.setSeed((long)this.m_parameter.getSeed());
    }

    public void createTrialVector() {
        for(int i = 0; i < this.m_parameter.getSwarmSize(); ++i) {
            int Irand = this.m_rand.nextInt(this.m_parameter.getGenomeDimension());

            for(int j = 0; j < this.m_parameter.getGenomeDimension(); ++j) {
                if (!(this.m_rand.nextDouble() <= this.m_parameter.getRecombinationCRfactor()) && Irand != j) {
                    this.m_trialVector[i][j] = this.m_genome[i].getTargetVector()[j];
                } else {
                    this.m_trialVector[i][j] = this.m_donorVector[i][j];
                }

                Irand = (Irand + 1) % this.m_parameter.getGenomeDimension();
            }

            this.overlapVector(this.m_trialVector[i], this.m_parameter.getGenomeDimension(), this.m_parameter.getMaxSNPNumber());
        }

    }

    public void overlapVector(int[] vector, int SNP_Number_Selected, int SNPnum) {
        for(int j = 0; j < SNP_Number_Selected; ++j) {
            for(int k = 0; k < SNP_Number_Selected; ++k) {
                boolean x;
                if (vector[j] == vector[k] && j != k) {
                    do {
                        x = false;
                        vector[j] = (int)(this.m_rand.nextDouble() * (double)SNPnum);

                        for(int m = 0; m < j; ++m) {
                            if (vector[j] == vector[m]) {
                                x = true;
                                break;
                            }
                        }
                    } while(x);
                }
            }
        }

    }
}
