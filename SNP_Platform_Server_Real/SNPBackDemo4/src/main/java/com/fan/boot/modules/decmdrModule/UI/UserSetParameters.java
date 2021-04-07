package com.fan.boot.modules.decmdrModule.UI;

public class UserSetParameters {
    private final int DEFAULT_SEED = 1;
    private final int DEFAULT_SWARMSIZE = 100;
    private final int DEFAULT_MAXGENERATION = 300;
    private final double DEFAULT_MUTATIONFACTOR = 0.5D;
    private final double DEFAULT_RECOMBINATIONCRFACTOR = 0.5D;
    private final int DEFAULT_ORDER = 2;
    private int seed = 1;
    private int swarmSize = 100;
    private int maxGeneration = 300;
    private double mutationFactor = 0.5D;
    private double recombinationCRfactor = 0.5D;
    private int order = 2;

    // 新添加的属性，结果文件路径
    private String resDataPath;

    public UserSetParameters() {
    }

    public void setSeed(int seed) {
        this.seed = seed != -1 ? seed : 1;
    }

    public void setSwarmSize(int size) {
        this.swarmSize = size != -1 ? size : 100;
    }

    public void setMaxGeneration(int generation) {
        this.maxGeneration = generation != -1 ? generation : 300;
    }

    public void setMutationFactor(double factor) {
        this.mutationFactor = factor != -1.0D ? factor : 0.5D;
    }

    public void setRecombinationCRfactor(double factor) {
        this.recombinationCRfactor = factor != -1.0D ? factor : 0.5D;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getSeed() {
        return this.seed;
    }

    public int getSwarmSize() {
        return this.swarmSize;
    }

    public int getMaxGeneration() {
        return this.maxGeneration;
    }

    public double getMutationFactor() {
        return this.mutationFactor;
    }

    public double getRecombinationCRfactor() {
        return this.recombinationCRfactor;
    }

    public int getOrder() {
        return this.order;
    }

    public String getResDataPath() {
        return resDataPath;
    }

    public void setResDataPath(String resDataPath) {
        this.resDataPath = resDataPath;
    }
}
