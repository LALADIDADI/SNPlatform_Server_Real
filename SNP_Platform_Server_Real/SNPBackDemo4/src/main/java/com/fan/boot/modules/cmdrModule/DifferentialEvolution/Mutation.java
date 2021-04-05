package com.fan.boot.modules.cmdrModule.DifferentialEvolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Mutation implements I_Mutation {
    private TargetVector[] m_genome = null;
    private int[][] m_donorVector = null;
    private int[][][] m_threeVector = null;
    private Random m_rand = null;
    private ArrayList<Integer> SwarmIndex = null;
    private AlogrithmParameter m_parameter = null;

    public void setGenome(TargetVector[] genome) {
        this.m_genome = (TargetVector[])genome.clone();
    }

    public int[][] getDonorVector() {
        return this.m_donorVector;
    }

    public Mutation(AlogrithmParameter parameter) {
        this.m_parameter = parameter;
        this.init();
    }

    private void init() {
        this.m_rand = new Random();
        this.m_rand.setSeed((long)this.m_parameter.getSeed());
        this.m_donorVector = new int[this.m_parameter.getSwarmSize()][this.m_parameter.getGenomeDimension()];
        this.m_threeVector = new int[this.m_parameter.getSwarmSize()][3][this.m_parameter.getGenomeDimension()];
        this.createRandomSwarmIndex();
    }

    private void createRandomSwarmIndex() {
        this.SwarmIndex = new ArrayList();

        for(int i = 0; i < this.m_parameter.getSwarmSize(); ++i) {
            this.SwarmIndex.add(i);
        }

        Collections.shuffle(this.SwarmIndex, this.m_rand);
    }

    public void createDonorVector() {
        this.createFourVectors();

        for(int i = 0; i < this.m_parameter.getSwarmSize(); ++i) {
            for(int j = 0; j < this.m_parameter.getGenomeDimension(); ++j) {
                this.m_donorVector[i][j] = (int)((double)this.m_threeVector[i][0][j] + this.F(this.m_threeVector[i][1][j] - this.m_threeVector[i][2][j]) + 0.5D);
                this.m_donorVector[i][j] = this.m_donorVector[i][j] >= this.m_parameter.getMaxSNPNumber() ? this.m_parameter.getMaxSNPNumber() - 1 : (this.m_donorVector[i][j] < 0 ? 0 : this.m_donorVector[i][j]);
            }
        }

    }

    private double F(int v) {
        return this.m_parameter.getMutationFactor() * (double)v;
    }

    public void createFourVectors() {
        int nowIndex;

        for(int i = 0; i < this.m_parameter.getSwarmSize(); ++i) {
            nowIndex = this.getIndex(i);
            boolean flag = nowIndex < this.m_parameter.getSwarmSize() / 2;
            int[][] var10000 = this.m_threeVector[i];
            TargetVector[] var10002 = this.m_genome;
            ArrayList var10003 = this.SwarmIndex;
            int var10004;
            if (flag) {
                ++nowIndex;
                var10004 = nowIndex;
            } else {
                --nowIndex;
                var10004 = nowIndex;
            }

            var10000[0] = var10002[(Integer)var10003.get(var10004)].getTargetVector();
            var10000 = this.m_threeVector[i];
            var10002 = this.m_genome;
            var10003 = this.SwarmIndex;
            if (flag) {
                ++nowIndex;
                var10004 = nowIndex;
            } else {
                --nowIndex;
                var10004 = nowIndex;
            }

            var10000[1] = var10002[(Integer)var10003.get(var10004)].getTargetVector();
            var10000 = this.m_threeVector[i];
            var10002 = this.m_genome;
            var10003 = this.SwarmIndex;
            if (flag) {
                ++nowIndex;
                var10004 = nowIndex;
            } else {
                --nowIndex;
                var10004 = nowIndex;
            }

            var10000[2] = var10002[(Integer)var10003.get(var10004)].getTargetVector();
            Collections.shuffle(this.SwarmIndex, this.m_rand);
        }

    }

    private int getIndex(int index) {
        for(int i = 0; i < this.SwarmIndex.size(); ++i) {
            if ((Integer)this.SwarmIndex.get(i) == index) {
                return i;
            }
        }

        return -1;
    }
}
