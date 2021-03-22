package com.fan.boot.modules.fastl;

import java.io.FileWriter;
import java.io.IOException;

public class ListAscendStore {
    public ListMutipleElement[] list;
    public int idx = 0;
    private int low = 0;
    private int high = 1;
    private int id = 0;
    public static String enter = "\n";

    public ListAscendStore(int _n, int _size) {
        this.list = new ListMutipleElement[_n];

        for(int i = 0; i < _n; ++i) {
            this.list[i] = new ListMutipleElement(_size);
        }

        this.idx = 0;
    }

    public void add(int[] _comb, double _val) {
        this.low = 0;
        this.high = this.idx;
        this.id = this.idx;

        while(this.low < this.high) {
            this.id = (this.low + this.high) / 2;
            if (_val < this.list[this.id].value) {
                this.high = this.id;
            } else {
                if (!(_val > this.list[this.id].value)) {
                    break;
                }

                this.low = this.id + 1;
            }
        }

        this.id = (this.low + this.high) / 2;
        if (this.id >= this.idx || !this.list[this.id].equal(_comb)) {
            int i;
            if (this.idx < this.list.length) {
                for(i = this.idx; i > this.id; --i) {
                    this.list[i].Set(this.list[i - 1].list, this.list[i - 1].value);
                }

                ++this.idx;
                this.list[this.id].Set(_comb, _val);
            } else {
                if (this.id >= this.idx) {
                    return;
                }

                for(i = this.idx - 1; i > this.id; --i) {
                    this.list[i].Set(this.list[i - 1].list, this.list[i - 1].value);
                }

                this.list[this.id].Set(_comb, _val);
            }

        }
    }

    public void clean() {
        this.idx = 0;
    }

    public ListMutipleElement getTop() {
        return this.idx == 0 ? null : this.list[0];
    }

    public void printAll() {
        for(int i = 0; i < this.idx; ++i) {
            this.list[i].print();
        }

    }

    public void printAll(FileWriter fwR) throws IOException {
        for(int i = 0; i < this.idx; ++i) {
            fwR.write(Integer.toString(i + 1) + "\t");
            fwR.write(this.list[i].getString());
            fwR.write(enter);
        }

    }

    public void printAll(FileWriter fwR, double _cv) throws IOException {
        for(int i = 0; i < this.idx && !(this.list[i].value > _cv); ++i) {
            fwR.write(Integer.toString(i + 1) + "\t");
            fwR.write(this.list[i].getString());
            fwR.write(enter);
        }

    }
}
