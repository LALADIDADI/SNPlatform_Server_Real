package com.fan.boot.modules.decmdrModule.DifferentialEvolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Initialization {
    private TargetVector[] m_Genome = null;
    private Random m_rand = null;
    private ArrayList<Integer> SNP = null;
    private AlogrithmParameter m_parameter = null;

    public Initialization(AlogrithmParameter parameter) {
        this.m_parameter = parameter;
    }

    public TargetVector[] initailGenome() {
        this.m_rand = new Random();
        this.m_rand.setSeed((long)this.m_parameter.getSeed());
        this.m_Genome = new TargetVector[this.m_parameter.getSwarmSize()];
        this.CreateSetOfSNPs();

        for(int i = 0; i < this.m_parameter.getSwarmSize(); ++i) {
            this.m_Genome[i] = new TargetVector();
            this.m_Genome[i].setTargetVector(this.createGenome());
        }

        return this.m_Genome;
    }

    private void CreateSetOfSNPs() {
        this.SNP = new ArrayList();

        for(int i = 0; i < this.m_parameter.getMaxSNPNumber(); ++i) {
            this.SNP.add(i);
        }

    }

    private int[] createGenome() {
        int[] genome = new int[this.m_parameter.getGenomeDimension()];
        Collections.shuffle(this.SNP, this.m_rand);

        for(int i = 0; i < this.m_parameter.getGenomeDimension(); ++i) {
            genome[i] = (Integer)this.SNP.get(i);
        }

        return genome;
    }
}
