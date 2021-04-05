
package com.fan.boot.modules.dcheModule.fastl;

public class Datag {
    public int[][] iCase;
    public int[][] iCont;
    public int nEleCase;
    public int nEleCont;
    public int nSNP;
    private int idxCase = 0;
    private int idxCont = 0;
    private static int len = 32;
    private static int mask = 1;
    private static String strCase = "1";
    private static int[] wordbit = new int[256];
    private static String SPLIT = " ";
    private int[] tempCase;
    private int[] tempCont;
    private int[] arrStatus = new int[5];
    private int idxStatus = 0;
    private boolean flagStatus = true;

    public Datag(int _nCase, int _nCont, int _nSNP) {
        this.nEleCase = (int)Math.ceil((double)_nCase / (double)len);
        this.nEleCont = (int)Math.ceil((double)_nCont / (double)len);
        this.iCase = new int[_nSNP * 3][this.nEleCase];
        this.iCont = new int[_nSNP * 3][this.nEleCont];
        this.tempCase = new int[this.nEleCase];
        this.tempCont = new int[this.nEleCont];
        this.nSNP = _nSNP;
        precompute();
    }

    public void add(String _str) {
        String[] arrStr = _str.split(SPLIT);
        int[] var10000;
        int var10001;
        int i;
        int temp;
        if (arrStr[0].equals(strCase)) {
            for(i = 0; i < this.nSNP; ++i) {
                temp = Integer.parseInt(arrStr[i + 1]);
                if (temp < 3) {
                    var10000 = this.iCase[i * 3 + temp];
                    var10001 = this.idxCase / len;
                    var10000[var10001] |= mask << this.idxCase % len;
                }
            }

            ++this.idxCase;
        } else {
            for(i = 0; i < this.nSNP; ++i) {
                temp = Integer.parseInt(arrStr[i + 1]);
                if (temp < 3) {
                    var10000 = this.iCont[i * 3 + temp];
                    var10001 = this.idxCont / len;
                    var10000[var10001] |= mask << this.idxCont % len;
                }
            }

            ++this.idxCont;
        }

    }

    private static int bitCount(int _i) {
        _i = (_i & 85) + (_i >> 1 & 85);
        _i = (_i & 51) + (_i >> 2 & 51);
        _i = (_i & 15) + (_i >> 4 & 15);
        return _i;
    }

    public void clean() {
        this.idxCase = 0;
        this.idxCont = 0;

        int i;
        int j;
        for(i = 0; i < this.iCase.length; ++i) {
            for(j = 0; j < this.iCase[0].length; ++j) {
                this.iCase[i][j] = 0;
            }
        }

        for(i = 0; i < this.iCont.length; ++i) {
            for(j = 0; j < this.iCont[0].length; ++j) {
                this.iCont[i][j] = 0;
            }
        }

    }

    private static int getCount(int[] _arr) {
        int result = 0;

        for(int i = 0; i < _arr.length; ++i) {
            result += wordbit[_arr[i] & 255] + wordbit[_arr[i] >> 8 & 255] + wordbit[_arr[i] >> 16 & 255] + wordbit[_arr[i] >> 24 & 255];
        }

        return result;
    }

    public int getDistribution(int _id1, int _id2, int[][] _matrix, boolean[] _arrNotEmpty) {
        int _size = 0;

        for(int j = 0; j < 3; ++j) {
            for(int k = 0; k < 3; ++k) {
                int i;
                for(i = 0; i < this.nEleCase; ++i) {
                    this.tempCase[i] = this.iCase[_id1 * 3 + j][i] & this.iCase[_id2 * 3 + k][i];
                }

                for(i = 0; i < this.nEleCont; ++i) {
                    this.tempCont[i] = this.iCont[_id1 * 3 + j][i] & this.iCont[_id2 * 3 + k][i];
                }

                _matrix[0][_size] = getCount(this.tempCase);
                _matrix[1][_size] = getCount(this.tempCont);
                if (_matrix[0][_size] != 0 || _matrix[1][_size] != 0) {
                    _arrNotEmpty[_size] = true;
                    ++_size;
                }
            }
        }

        return _size;
    }

    public int getDistribution(int[] _comb, int _size, int[][] _matrix, boolean[] _arrNotEmpty) {
        int idx = 0;
        int nComb = (int)Math.pow(3.0D, (double)_size);

        int i;
        for(i = 0; i < this.arrStatus.length; ++i) {
            this.arrStatus[i] = 0;
        }

        do {
            for(i = 0; i < this.nEleCase; ++i) {
                this.tempCase[i] = -1;
            }

            for(i = 0; i < this.nEleCont; ++i) {
                this.tempCont[i] = -1;
            }

            for(i = 0; i < _size; ++i) {
                int[] var10000;
                int j;
                for(j = 0; j < this.nEleCase; ++j) {
                    var10000 = this.tempCase;
                    var10000[j] &= this.iCase[_comb[i] * 3 + this.arrStatus[i]][j];
                }

                for(j = 0; j < this.nEleCont; ++j) {
                    var10000 = this.tempCont;
                    var10000[j] &= this.iCont[_comb[i] * 3 + this.arrStatus[i]][j];
                }
            }

            _matrix[0][idx] = getCount(this.tempCase);
            _matrix[1][idx] = getCount(this.tempCont);
            if (_matrix[0][idx] != 0 || _matrix[1][idx] != 0) {
                _arrNotEmpty[idx] = true;
                ++idx;
            }

            --nComb;
            if (nComb == 0) {
                break;
            }

            this.idxStatus = _size - 1;

            while(this.flagStatus) {
                int var10002 = this.arrStatus[this.idxStatus]++;
                if (this.arrStatus[this.idxStatus] > 2) {
                    this.arrStatus[this.idxStatus] = 0;
                    --this.idxStatus;
                } else {
                    this.flagStatus = false;
                }
            }

            this.flagStatus = true;
        } while(nComb > 0);

        return idx;
    }

    private static void precompute() {
        for(int i = 0; i < wordbit.length; ++i) {
            wordbit[i] = bitCount(i);
        }

    }
}
