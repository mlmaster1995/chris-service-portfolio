package com.chris.dto;

/**
 * base layer of the Dto object
 *
 * @param <T>
 */
public abstract class BaseDtoToEntity<T> {

    public abstract T toEntity();

    public abstract boolean isValid();
}
