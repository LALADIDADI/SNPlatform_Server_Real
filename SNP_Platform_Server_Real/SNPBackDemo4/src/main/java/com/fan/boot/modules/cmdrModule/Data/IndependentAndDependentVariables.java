package com.fan.boot.modules.cmdrModule.Data;

import java.util.ArrayList;

public class IndependentAndDependentVariables {
    ArrayList<ColumnInformation<?>> m_independentVariable = null;
    ColumnInformation<?> m_dependentVariable = null;

    public IndependentAndDependentVariables() {
        this.m_independentVariable = new ArrayList();
    }

    public void addIndependentVariable(ColumnInformation<?> column) {
        this.m_independentVariable.add(column);
    }

    public void setDependentVariable(ColumnInformation<?> column) {
        this.m_dependentVariable = column;
    }

    public ArrayList<ColumnInformation<?>> getIndependentVariable() {
        return this.m_independentVariable;
    }

    public ColumnInformation<?> getIndependentVariable(int index) {
        return (ColumnInformation)this.m_independentVariable.get(index);
    }

    public ColumnInformation<?> getDependentVariable() {
        return this.m_dependentVariable;
    }
}
