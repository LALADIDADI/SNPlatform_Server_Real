package com.fan.boot.modules.cmdrModule.UI;

public class AbstractCategorical implements AbstractType {
    public AbstractCategorical() {
    }

    public AbstractType getType() {
        return new AbstractCategorical();
    }

    public String toString() {
        return "Categorical";
    }
}
