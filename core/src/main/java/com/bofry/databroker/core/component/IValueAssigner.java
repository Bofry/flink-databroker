package com.bofry.databroker.core.component;

import java.io.Serializable;
import java.lang.reflect.Field;

public interface IValueAssigner extends Serializable {

    void init();

    void assign(Object o, Field f);

}
