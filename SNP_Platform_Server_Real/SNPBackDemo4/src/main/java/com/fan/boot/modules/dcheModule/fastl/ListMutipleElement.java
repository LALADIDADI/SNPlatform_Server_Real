package com.fan.boot.modules.dcheModule.fastl;

public class ListMutipleElement {
    public int[] list;
    public double value;
    public static String tab = "\t";

    public ListMutipleElement(int _n) {
        this.list = new int[_n];
    }

    public boolean equal(ListMutipleElement _ele) {
        for(int i = 0; i < this.list.length; ++i) {
            if (this.list[i] != _ele.list[i]) {
                return false;
            }
        }

        return true;
    }

    public boolean equal(int[] _comb) {
        for(int i = 0; i < this.list.length; ++i) {
            if (this.list[i] != _comb[i]) {
                return false;
            }
        }

        return true;
    }

    public String getString() {
        StringBuffer strBuf = new StringBuffer();

        for(int i = 0; i < this.list.length; ++i) {
            strBuf.append(this.list[i] + tab);
        }

        strBuf.append(this.value);
        return strBuf.toString();
    }

    public void Set(int[] _comb, double _val) {
        for(int i = 0; i < this.list.length; ++i) {
            this.list[i] = _comb[i];
        }

        this.value = _val;
    }

    public void Sort() {
        int idx;
        int temp;

        for(int i = 0; i < this.list.length; ++i) {
            idx = i;

            for(int j = i + 1; j < this.list.length; ++j) {
                if (this.list[j] < this.list[idx]) {
                    idx = j;
                }
            }

            if (idx != i) {
                temp = this.list[i];
                this.list[i] = this.list[idx];
                this.list[idx] = temp;
            }
        }

    }

    public void print() {
        for(int i = 0; i < this.list.length; ++i) {
            System.out.print(this.list[i] + "\t");
        }

        System.out.println(this.value);
    }
}
