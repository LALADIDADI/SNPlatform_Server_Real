package com.fan.boot.modules.mtif;

import java.util.Comparator;

class SortByScore implements Comparator<SNP> {
    SortByScore() {
    }

    public int compare(SNP s1, SNP s2) {
        if (s1.getScore() < s2.getScore()) {
            return 1;
        } else {
            return s1.getScore() == s2.getScore() ? 0 : -1;
        }
    }
}
