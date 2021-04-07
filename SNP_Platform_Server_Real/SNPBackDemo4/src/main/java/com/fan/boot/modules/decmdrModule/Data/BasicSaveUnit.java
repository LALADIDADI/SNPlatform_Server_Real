package com.fan.boot.modules.decmdrModule.Data;

public class BasicSaveUnit {
    public nfoldData m_nfold_data = null;
    public int m_nfold = 0;
    public int m_lower_Order = 0;
    public int m_upper_Order = 0;
    public OutComeVariable m_outComeVariable = null;

    public BasicSaveUnit() {
    }

    public void setnfoldData(nfoldData data) {
        this.m_nfold_data = data;
    }

    public nfoldData getnfoldData() {
        return this.m_nfold_data != null ? this.m_nfold_data : null;
    }

    public int getColumnSize() {
        return this.m_nfold_data != null ? this.m_nfold_data.getHeadings().length : 0;
    }

    public int getSampleSize() {
        return this.m_nfold_data != null ? this.m_nfold_data.getSampleSize() : 0;
    }

    public void setnfold(int n) {
        this.m_nfold = n;
    }

    public int getnfold() {
        return this.m_nfold;
    }

    public void setLower_Order(int lower) {
        this.m_lower_Order = lower;
    }

    public int getLower_Order() {
        return this.m_lower_Order;
    }

    public void setUpper_Order(int upper) {
        this.m_upper_Order = upper;
    }

    public int getUpper_Order() {
        return this.m_upper_Order;
    }

    public void setOutComeVariable(OutComeVariable outCome) {
        this.m_outComeVariable = outCome;
    }

    public OutComeVariable getOutComeVariable() {
        return this.m_outComeVariable != null ? this.m_outComeVariable : null;
    }
}
