package com.fan.boot.modules.fastl;

public class ListDescendList {
    public ListAscendListElement[] list = null;
    public int size;
    public int idx;
    public int low;
    public int high;
    public int id;
    public ListAscendListElement p = new ListAscendListElement();
    private double Thre = 0.01D;
    private int count;

    public ListDescendList(int _size) {
        this.list = new ListAscendListElement[_size];
        this.size = _size;
        this.low = 0;
        this.high = 0;
        this.idx = 0;

        for(int i = 0; i < this.size; ++i) {
            this.list[i] = new ListAscendListElement();
        }

    }

    public void add(int _id1, int _id2, double _val) {
        if (!(_val < this.Thre)) {
            this.low = 0;
            this.high = this.idx;
            this.id = this.idx;

            while(this.low < this.high) {
                this.id = (this.low + this.high) / 2;
                if (_val > this.list[this.id].value) {
                    this.high = this.id;
                } else {
                    if (!(_val < this.list[this.id].value)) {
                        break;
                    }

                    this.low = this.id + 1;
                }
            }

            this.id = (this.low + this.high) / 2;
            int i;
            if (this.idx < this.list.length) {
                for(i = this.idx; i > this.id; --i) {
                    this.list[i].set(this.list[i - 1].id1, this.list[i - 1].id2, this.list[i - 1].value);
                }

                ++this.idx;
                this.list[this.id].set(_id1, _id2, _val);
            } else {
                if (this.id >= this.idx) {
                    return;
                }

                for(i = this.idx - 1; i > this.id; --i) {
                    this.list[i].set(this.list[i - 1].id1, this.list[i - 1].id2, this.list[i - 1].value);
                }

                this.list[this.id].set(_id1, _id2, _val);
            }

        }
    }

    public void clean() {
        this.low = 0;
        this.high = 0;
        this.idx = 0;
    }

    public ListAscendListElement getTop(boolean[] _arrNotEmpty) {
        int i;
        for(i = 0; i < this.idx && (!_arrNotEmpty[this.list[i].id1] || !_arrNotEmpty[this.list[i].id2]); ++i) {
        }

        if (i >= this.idx) {
            this.idx = 0;
            return null;
        } else {
            this.p.set(this.list[i].id1, this.list[i].id2, this.list[i].value);

            for(int j = i + 1; j < this.idx; ++j) {
                this.list[j - i - 1].set(this.list[j].id1, this.list[j].id2, this.list[j].value);
            }

            this.idx -= i + 1;
            return this.p;
        }
    }

    public ListAscendListElement getTop() {
        if (this.idx == 0) {
            return null;
        } else {
            this.count = 0;
            this.p.set(this.list[0].id1, this.list[0].id2, this.list[0].value);

            int i;
            for(i = 0; i < this.idx; ++i) {
                if (this.list[i].id1 == this.p.id1 || this.list[i].id1 == this.p.id2 || this.list[i].id2 == this.p.id1 || this.list[i].id2 == this.p.id2) {
                    ++this.count;
                    this.list[i].id1 = -1;
                }
            }

            this.low = 0;
            this.high = 0;

            for(i = 0; i < this.idx - this.count; ++i) {
                if (this.list[i].id1 == -1) {
                    for(int j = this.high; j < this.idx; ++j) {
                        if (this.list[j].id1 != -1) {
                            this.high = j;
                            break;
                        }
                    }

                    this.list[i].set(this.list[this.high].id1, this.list[this.high].id2, this.list[this.high].value);
                    this.list[this.high].id1 = -1;
                    ++this.high;
                }
            }

            this.idx -= this.count;
            return this.p;
        }
    }
}
