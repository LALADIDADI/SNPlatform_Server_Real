package com.fan.boot.modules.decmdrModule.Method;

import com.fan.boot.modules.decmdrModule.Data.ClassificationMethodResultModel;

public class CheckClassificationMethod {
    public CheckClassificationMethod() {
    }

    public ClassificationMethod getClassificationMethod(Object method) {
        return method instanceof ClassificationMethodResultModel ? new DECMDR((ClassificationMethodResultModel)method) : null;
    }
}
