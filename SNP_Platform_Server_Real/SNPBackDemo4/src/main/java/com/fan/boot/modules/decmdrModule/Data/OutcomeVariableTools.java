package com.fan.boot.modules.decmdrModule.Data;

import java.util.Iterator;
import java.util.Map;

public class OutcomeVariableTools {
    private String m_heading = "";
    private String[] m_itemNames = null;
    private int[] m_cetagoryNumber = null;
    private int[] m_numberOfType = null;

    public OutcomeVariableTools(CategoryColumn column, String nfold) {
        Map<String, Integer> Category = column.getCategoryCount();
        this.m_heading = column.getHeading();
        this.m_itemNames = new String[Category.keySet().size()];
        this.m_cetagoryNumber = new int[Category.keySet().size()];
        this.m_numberOfType = new int[Category.keySet().size()];
        int index = 0;

        for(Iterator var6 = Category.entrySet().iterator(); var6.hasNext(); ++index) {
            Map.Entry<String, Integer> entry = (Map.Entry)var6.next();
            this.m_itemNames[index] = (String)entry.getKey();
            this.m_cetagoryNumber[index] = (Integer)entry.getValue();
            this.m_numberOfType[index] = this.m_cetagoryNumber[index] / (nfold.equals("5-fold") ? 5 : 10);
        }

    }

    public String getHeading() {
        return this.m_heading;
    }

    public String[] getCategoryName() {
        return this.m_itemNames;
    }

    public int[] getCategoryNumber() {
        return this.m_cetagoryNumber;
    }

    public int[] getNumberOfType() {
        return this.m_numberOfType;
    }

    public String getCategoryName(int index) {
        return this.m_itemNames[index];
    }

    public int getCategoryNumber(int index) {
        return this.m_cetagoryNumber[index];
    }

    public int getNumberOfType(int index) {
        return this.m_numberOfType[index];
    }

    public int getNumberOfType(String name) {
        for(int i = 0; i < this.m_itemNames.length; ++i) {
            if (this.m_itemNames[i].equals(name)) {
                return this.m_numberOfType[i];
            }
        }

        return 0;
    }
}
