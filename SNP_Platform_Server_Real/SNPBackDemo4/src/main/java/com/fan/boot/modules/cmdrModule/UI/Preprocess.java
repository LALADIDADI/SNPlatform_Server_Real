package com.fan.boot.modules.cmdrModule.UI;

import com.fan.boot.modules.cmdrModule.Data.ColumnInformation;
import com.fan.boot.modules.cmdrModule.Data.OutComeVariable;
import com.fan.boot.modules.cmdrModule.Data.nfoldData;
import com.fan.boot.modules.cmdrModule.Method.ClassificationMethod;
import com.fan.boot.modules.cmdrModule.Method.DECMDR;
import com.fan.boot.modules.cmdrModule.Method.LoadData;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class Preprocess {
    private ArrayList<ColumnInformation> dataset = null;
    private OutComeVariable outCome = null;
    private String path = "";
    private int order = 0;
    private String outComeString = "";

    public Preprocess(String path, String outComeString, UserSetParameters UserParameters) {
        this.path = path;
        this.order = UserParameters.getOrder();
        this.outComeString = outComeString;
        this.openDataSet();
        this.init_DECMDR(UserParameters);
    }

    private void init_DECMDR(UserSetParameters UserParameters) {
        Generate_nfoldData GND = new Generate_nfoldData();
        GND.doGeneratenfoldData(this.dataset, this.outCome, UserParameters.getSeed());
        this.init_DECMDR(GND.getnfoldData(), UserParameters);
    }

    private void openDataSet() {
        File f = new File(this.path);
        LoadData load = new LoadData();
        this.dataset = (load.new readFile(f.getPath())).getColumnData();
        this.setOutCome(this.outComeString);
    }

    private void setOutCome(String attribute) {
        if (attribute != null) {
            Iterator var3 = this.dataset.iterator();

            while(var3.hasNext()) {
                ColumnInformation column = (ColumnInformation)var3.next();
                if (attribute.equals(column.getHeading())) {
                    this.outCome = new OutComeVariable((String[])column.getColumnValues(), column.getHeading());
                }
            }
        }

    }

    private void init_DECMDR(nfoldData nfold_data, UserSetParameters UserParameters) {
        if (nfold_data != null) {
            ClassificationMethod nowRunClassificationMethod = new DECMDR("DECMDR", nfold_data);
            nowRunClassificationMethod.setLower_Order(this.order);
            nowRunClassificationMethod.setUpper_Order(this.order);
            nowRunClassificationMethod.setOutComeVariable(this.outCome);
            // nowRunClassificationMethod.implement(this.path.substring(0, this.path.lastIndexOf("del")) + "_DE.txt", UserParameters);
            // 这里改写了，可以从入口修改结果文件返回位置
            nowRunClassificationMethod.implement(UserParameters.getResDataPath(), UserParameters);
        }

    }

    public ArrayList<ColumnInformation> getPreprocessDataSet() {
        return this.dataset;
    }

    public OutComeVariable getOutComeVariable() {
        return this.outCome;
    }
}
