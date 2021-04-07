package com.fan.boot.modules.decmdrModule.Method;

import com.fan.boot.modules.decmdrModule.BasicOperation.AverageNfoldResult;
import com.fan.boot.modules.decmdrModule.BasicOperation.BestResult;
import com.fan.boot.modules.decmdrModule.Data.BasicSaveUnit;
import com.fan.boot.modules.decmdrModule.Data.DefaultModelResult;
import com.fan.boot.modules.decmdrModule.Data.HeadingAndType;
import com.fan.boot.modules.decmdrModule.Data.OutComeVariable;
import com.fan.boot.modules.decmdrModule.Data.nfoldData;
import com.fan.boot.modules.decmdrModule.UI.AbstractCategorical;
import com.fan.boot.modules.decmdrModule.UI.UserSetParameters;
import java.util.Iterator;
import javax.swing.JButton;

public class ClassificationMethod extends BasicSaveUnit {
    public DefaultModelResult m_results = null;
    public Thread m_thread_start = null;
    public JButton m_btn_start = null;
    public JButton m_btn_stop = null;

    public ClassificationMethod(DefaultModelResult model) {
        this.setClassificationMethodModel(model);
    }

    public ClassificationMethod(DefaultModelResult model, String name) {
        this.m_results = model;
        this.m_results.setResultName(name);
    }

    public ClassificationMethod(DefaultModelResult model, String name, nfoldData nfold_data) {
        this.m_results = model;
        this.m_results.setResultName(name);
        this.m_nfold_data = nfold_data;
        this.m_results.setnfoldData(nfold_data);
        this.m_nfold = nfold_data.getnfoldData().keySet().size();
        this.m_results.setnfold(this.m_nfold);
    }

    public Thread getThread_start() {
        return this.m_thread_start;
    }

    public void implement(String path, UserSetParameters UserParameters) {
    }

    public void setLower_Order(int lower) {
        this.m_lower_Order = lower;
        this.m_results.setLower_Order(lower);
    }

    public void setUpper_Order(int upper) {
        this.m_upper_Order = upper;
        this.m_results.setUpper_Order(upper);
    }

    public void setOutComeVariable(OutComeVariable outCome) {
        this.m_outComeVariable = outCome;
        this.m_results.setOutComeVariable(outCome);
    }

    public void generateBasicDataInformation(String method_name) {
        this.m_results.appendResult("******Run information******");
        this.m_results.appendResult("Start time:\t" + this.m_results.getTimeforHHMMSS());
        this.m_results.appendResult("Scheme:\t" + method_name + "\tseed: " + this.m_nfold_data.getSeed());
        this.m_results.appendResult("Instances:\t" + this.m_nfold_data.getSampleSize());
        this.m_results.appendResult("Attributes:\t" + (this.m_nfold_data.getHeadings().length - 1));
        HeadingAndType[] var5;
        int var4 = (var5 = this.m_nfold_data.getHeadingAndType()).length;

        for(int var3 = 0; var3 < var4; ++var3) {
            HeadingAndType<?> att = var5[var3];
            if (!att.getHeading().equals(this.m_outComeVariable.getHeading())) {
                this.m_results.appendResult("\t\t" + att.getHeading() + "\t" + (att.getType() instanceof AbstractCategorical ? "Categorical variable" : "Numerical variable"));
            }
        }

        String outComeCategoryItems = "";

        String s;
        for(Iterator var8 = this.m_outComeVariable.getCategoryLabel().iterator(); var8.hasNext(); outComeCategoryItems = outComeCategoryItems + s + ", ") {
            s = (String)var8.next();
        }

        this.m_results.appendResult("Outcome:\t" + this.m_outComeVariable.getHeading() + " {" + outComeCategoryItems.substring(0, outComeCategoryItems.length() - 2) + "}");
        this.m_results.appendResult("Test mode:\t" + this.m_nfold + "-fold cross-validation");
        this.m_results.appendResult("\t\tDetecting " + this.m_lower_Order + " to " + this.m_upper_Order + " orders");
        this.m_results.appendResult("");
    }

    public void generateBestResult(BestResult result) {
        AverageNfoldResult average_nfold_result = result.getAverageNfoldResult();

        for(int i = 1; i <= average_nfold_result.getErrorRate().length; ++i) {
            this.m_results.appendResult(i + "-fold:\t" + average_nfold_result.getErrorRate(i));
        }

        this.m_results.appendResult("Average:\t" + average_nfold_result.getAverageErrorRate());
    }

    public DefaultModelResult getModelResult() {
        return this.m_results;
    }

    public void setClassificationMethodModel(DefaultModelResult model) {
        this.m_results = model;
        this.m_nfold_data = model.getnfoldData();
        this.m_nfold = model.getnfold();
        this.m_lower_Order = model.getLower_Order();
        this.m_upper_Order = model.getUpper_Order();
    }

    public void setStopAndStartButton(JButton start, JButton stop) {
        this.m_btn_start = start;
        this.m_btn_stop = stop;
    }
}
