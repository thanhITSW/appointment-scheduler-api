package com.appointment.jpa.specifications.filter;

import java.io.Serializable;

/**
 * Filter for enum types. It can be added to a criteria class as a member, to support the
 * following query parameters:
 * <pre>
 *      fieldName.equals='ENUM_VALUE'
 *      fieldName.notEquals='ENUM_VALUE'
 *      fieldName.specified=true
 *      fieldName.specified=false
 *      fieldName.in='ENUM_VALUE1','ENUM_VALUE2'
 *      fieldName.notIn='ENUM_VALUE1','ENUM_VALUE2'
 * </pre>
 */
public class EnumFilter<T extends Enum<T>> extends Filter<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public EnumFilter() {
    }

    public EnumFilter(EnumFilter<T> filter) {
        super(filter);
    }

    @Override
    public EnumFilter<T> copy() {
        return new EnumFilter<>(this);
    }
}
