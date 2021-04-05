package com.fan.boot.modules.cmdrModule.Method;

import com.fan.boot.modules.cmdrModule.BasicOperation.nfoldDataTools;
import com.fan.boot.modules.cmdrModule.Data.ClassificationMethodResultModel;
import com.fan.boot.modules.cmdrModule.Data.nfoldData;
import com.fan.boot.modules.cmdrModule.DifferentialEvolution.AlogrithmParameter;
import com.fan.boot.modules.cmdrModule.DifferentialEvolution.DifferentialEvolution;
import com.fan.boot.modules.cmdrModule.DifferentialEvolution.TargetVector;
import com.fan.boot.modules.cmdrModule.UI.UserSetParameters;

public class DECMDR extends ClassificationMethod {
    AlogrithmParameter parameter = null;

    public DECMDR(String name) {
        super(new ClassificationMethodResultModel(), name);
    }

    public DECMDR(String name, nfoldData nfold_data) {
        super(new ClassificationMethodResultModel(), name, nfold_data);
    }

    public DECMDR(ClassificationMethodResultModel model) {
        super(model);
    }

    public void implement(String outpath, UserSetParameters UserParameters) {
        this.generateBasicDataInformation("DECMDR");

        for(int order = this.getLower_Order(); order <= this.getUpper_Order(); ++order) {
            long StartTime = System.currentTimeMillis();
            this.parameter = this.generateAlgorithmParameter(order, UserParameters);
            DifferentialEvolution DE = new DifferentialEvolution(this.parameter);
            TargetVector[] results = DE.implementDE();
            this.evaluateResultAndSaveResult(results, outpath);
            long EndTime = System.currentTimeMillis();
            System.out.println("Implement Time: " + (EndTime - StartTime) / 1000L + " (s)");
        }

    }

    private AlogrithmParameter generateAlgorithmParameter(int order, UserSetParameters UserParameters) {
        AlogrithmParameter parameters = new AlogrithmParameter();
        parameters.setSeed(UserParameters.getSeed());
        parameters.setSwarmSize(UserParameters.getSwarmSize());
        parameters.setGenomeDimension(order);
        parameters.setMaxGeneration(UserParameters.getMaxGeneration());
        parameters.setMutationFactor(UserParameters.getMutationFactor());
        parameters.setRecombinationCRfactor(UserParameters.getRecombinationCRfactor());
        parameters.setNfoldData(this.generate_nfoldDataTools());
        parameters.setmaxSNPNumber(parameters.getNfoldData().getColumnSize() - 1);
        return parameters;
    }

    private nfoldDataTools generate_nfoldDataTools() {
        nfoldDataTools nfoldTools = new nfoldDataTools(this.m_nfold_data);
        nfoldTools.setOutComeVariable(this.m_outComeVariable);
        nfoldTools.setnfold(this.m_nfold);
        nfoldTools.generateColumnData();
        return nfoldTools;
    }

    private void evaluateResultAndSaveResult(TargetVector[] targetVectors, String outpath) {
        TargetVector bestTargetVector = this.getBestTargetVector(targetVectors);
        bestTargetVector.getResult().setnfoldDataTools(this.parameter.getNfoldData());
        System.out.println(bestTargetVector.getResult().showResult(outpath));
    }

    private TargetVector getBestTargetVector(TargetVector[] targetVectors) {
        TargetVector targe = targetVectors[0];

        for(int i = 0; i < targetVectors.length; ++i) {
            if (targe.getResult().getAverageErrorRate() >= targetVectors[i].getResult().getAverageErrorRate()) {
                targe = targetVectors[i];
            }
        }

        return targe;
    }
}
