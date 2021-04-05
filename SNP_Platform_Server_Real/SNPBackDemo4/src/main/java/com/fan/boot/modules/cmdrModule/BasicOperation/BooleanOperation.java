package com.fan.boot.modules.cmdrModule.BasicOperation;

import com.fan.boot.modules.cmdrModule.Data.OutComeVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BooleanOperation {
    private ArrayList<boolean[]> m_booleanArrays = null;
    private OutComeVariable m_outcome = null;

    public BooleanOperation(ArrayList<boolean[]> booleanArrays, OutComeVariable outcome) {
        this.m_booleanArrays = booleanArrays;
        this.m_outcome = outcome;
    }

    public Map<String, Integer> countBoolean() {
        int length = ((boolean[])this.m_booleanArrays.get(0)).length;
        Map<String, Integer> count_Match = new HashMap();
        String[] outcomeName = (String[])this.m_outcome.getColumnValues();
        Object[] var7;
        int index = (var7 = this.m_outcome.getCategoryLabel().toArray()).length;

        for(int var5 = 0; var5 < index; ++var5) {
            Object s = var7[var5];
            count_Match.put(s.toString(), 0);
        }

        for(int booleanArrays_index = 0; booleanArrays_index < length; ++booleanArrays_index) {
            boolean flag = true;

            for(index = 0; index < this.m_booleanArrays.size(); ++index) {
                if (!((boolean[])this.m_booleanArrays.get(index))[booleanArrays_index]) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                count_Match.put(outcomeName[booleanArrays_index], (Integer)count_Match.get(outcomeName[booleanArrays_index]) + 1);
            }
        }

        return count_Match;
    }

    public static void main(String[] s) {
        boolean[] a = new boolean[]{true, false, true, true, false};
        boolean[] b = new boolean[]{true, false, true, false, true};
        String[] name = new String[]{"0.0", "1.0", "0.0", "0.0", "1.0"};
        OutComeVariable outcome = new OutComeVariable(name, "example");
        ArrayList<boolean[]> c = new ArrayList();
        c.add(a);
        c.add(b);
        Map<String, Integer> results = (new BooleanOperation(c, outcome)).countBoolean();
        Iterator var8 = results.entrySet().iterator();

        while(var8.hasNext()) {
            Entry<String, Integer> entry = (Entry)var8.next();
            System.out.println("outcome = " + (String)entry.getKey() + "\t count = " + entry.getValue());
        }

    }
}
