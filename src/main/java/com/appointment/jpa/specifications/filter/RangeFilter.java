package com.appointment.jpa.specifications.filter;

import java.util.Objects;

/**
 * Filter class for Comparable types, where less than / greater than / etc relations could be interpreted. It can be
 * added to a criteria class as a member, to support the following query parameters:
 * <pre>
 *      fieldName.equals=42
 *      fieldName.notEquals=42
 *      fieldName.specified=true
 *      fieldName.specified=false
 *      fieldName.in=43,42
 *      fieldName.notIn=43,42
 *      fieldName.greaterThan=41
 *      fieldName.lessThan=44
 *      fieldName.greaterThanOrEqual=42
 *      fieldName.lessThanOrEqual=44
 * </pre>
 * Due to problems with the type conversions, the descendant classes should be used, where the generic type parameter
 * is materialized.
 *
 * @param <T> the type of filter.
 * @see IntegerFilter
 * @see DoubleFilter
 * @see FloatFilter
 * @see LongFilter
 * @see LocalDateFilter
 * @see InstantFilter
 * @see ShortFilter
 * @see ZonedDateTimeFilter
 */
public class RangeFilter<T extends Comparable<? super T>> extends Filter<T> {

    private transient T greaterThan;
    private transient T lessThan;
    private transient T greaterThanOrEqual;
    private transient T lessThanOrEqual;

    /**
     * <p>Constructor for RangeFilter.</p>
     */
    public RangeFilter() {
    }

    /**
     * <p>Constructor for RangeFilter.</p>
     *
     * @param filter a {@link RangeFilter} object.
     */
    public RangeFilter(final RangeFilter<T> filter) {
        super(filter);
        this.greaterThan = filter.greaterThan;
        this.lessThan = filter.lessThan;
        this.greaterThanOrEqual = filter.greaterThanOrEqual;
        this.lessThanOrEqual = filter.lessThanOrEqual;
    }

    /** {@inheritDoc} */
    @Override
    public RangeFilter<T> copy() {
        return new RangeFilter<>(this);
    }

    /**
     * <p>Getter for the field <code>greaterThan</code>.</p>
     *
     * @return a T object.
     */
    public T getGreaterThan() {
        return greaterThan;
    }

    /**
     * <p>Setter for the field <code>greaterThan</code>.</p>
     *
     * @param greaterThan a T object.
     * @return a {@link RangeFilter} object.
     */
    public RangeFilter<T> setGreaterThan(T greaterThan) {
        this.greaterThan = greaterThan;
        return this;
    }

    /**
     * <p>Getter for the field <code>lessThan</code>.</p>
     *
     * @return a T object.
     */
    public T getLessThan() {
        return lessThan;
    }

    /**
     * <p>Setter for the field <code>lessThan</code>.</p>
     *
     * @param lessThan a T object.
     * @return a {@link RangeFilter} object.
     */
    public RangeFilter<T> setLessThan(T lessThan) {
        this.lessThan = lessThan;
        return this;
    }

    /**
     * <p>Getter for the field <code>greaterThanOrEqual</code>.</p>
     *
     * @return a T object.
     */
    public T getGreaterThanOrEqual() {
        return greaterThanOrEqual;
    }

    /**
     * <p>Setter for the field <code>greaterThanOrEqual</code>.</p>
     *
     * @param greaterThanOrEqual a T object.
     * @return a {@link RangeFilter} object.
     */
    public RangeFilter<T> setGreaterThanOrEqual(T greaterThanOrEqual) {
        this.greaterThanOrEqual = greaterThanOrEqual;
        return this;
    }

    /**
     * <p>Getter for the field <code>lessThanOrEqual</code>.</p>
     *
     * @return a T object.
     */
    public T getLessThanOrEqual() {
        return lessThanOrEqual;
    }

    /**
     * <p>Setter for the field <code>lessThanOrEqual</code>.</p>
     *
     * @param lessThanOrEqual a T object.
     * @return a {@link RangeFilter} object.
     */
    public RangeFilter<T> setLessThanOrEqual(T lessThanOrEqual) {
        this.lessThanOrEqual = lessThanOrEqual;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final RangeFilter<?> that = (RangeFilter<?>) o;
        return Objects.equals(greaterThan, that.greaterThan) &&
            Objects.equals(lessThan, that.lessThan) &&
            Objects.equals(greaterThanOrEqual, that.greaterThanOrEqual) &&
            Objects.equals(lessThanOrEqual, that.lessThanOrEqual);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), greaterThan, lessThan, greaterThanOrEqual, lessThanOrEqual);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getFilterName() + " ["
            + (getEquals() != null ? "equals=" + getEquals() + ", " : "")
            + (getNotEquals() != null ? "notEquals=" + getNotEquals() + ", " : "")
            + (getSpecified() != null ? "specified=" + getSpecified() + ", " : "")
            + (getIn() != null ? "in=" + getIn() + ", " : "")
            + (getNotIn() != null ? "notIn=" + getNotIn() + ", " : "")
            + (getGreaterThan() != null ? "greaterThan=" + getGreaterThan() + ", " : "")
            + (getLessThan() != null ? "lessThan=" + getLessThan() + ", " : "")
            + (getGreaterThanOrEqual() != null ? "greaterThanOrEqual=" + getGreaterThanOrEqual() + ", " : "")
            + (getLessThanOrEqual() != null ? "lessThanOrEqual=" + getLessThanOrEqual() : "")
            + "]";
    }

}
