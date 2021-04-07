package com.fan.boot.modules.decmdrModule.Data;

import com.fan.boot.modules.decmdrModule.UI.AbstractType;

public interface Cell {
    AbstractType getType();

    void setHeading(String var1);

    String getHeading();

    HeadingAndType<?> getHeadingAndType();
}
