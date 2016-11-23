package com.enigmabridge.utils;

/**
 * Created by dusanklinec on 23.11.16.
 */
public class FieldWrapper<T> {
    protected T value;
    protected T defaultValue;
    protected boolean isDefault = true;

    public FieldWrapper(T value) {
        this.value = value;
        isDefault = false;
    }

    public FieldWrapper(T value, boolean isDefault) {
        if (isDefault) {
            this.defaultValue = value;
        }

        this.value = value;
        this.isDefault = isDefault;
    }

    public T getValue() {
        return value;
    }

    public T getDefault() {
        return defaultValue;
    }

    public boolean isDefault(){
        return isDefault;
    }

    public void setValue(T value) {
        this.value = value;
        isDefault = false;
    }

    public void reset(){
        this.value = this.defaultValue;
        isDefault = true;
    }

}
