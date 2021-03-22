package com.fan.boot.modules.fastl;

public class ListAscendListElement implements Comparable<ListAscendListElement> {
    public int id1;
    public int id2;
    public double value;

    public ListAscendListElement() {
    }

    public void set(int _id1, int _id2, double _value) {
        this.id1 = _id1;
        this.id2 = _id2;
        this.value = _value;
    }

    public int compareTo(ListAscendListElement arg0) {
        if (this.value > arg0.value) {
            return 1;
        } else {
            return this.value < arg0.value ? -1 : 0;
        }
    }
}
