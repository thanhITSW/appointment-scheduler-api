package com.appointment.jpa.specifications.filter;

/**
 * Filter class for {@link Float} type attributes.
 *
 * @see RangeFilter
 */
public class FloatFilter extends RangeFilter<Float> {

    /**
     * <p>Constructor for FloatFilter.</p>
     */
    public FloatFilter() {
    }

    /**
     * <p>Constructor for FloatFilter.</p>
     *
     * @param filter a {@link FloatFilter} object.
     */
    public FloatFilter(final FloatFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link FloatFilter} object.
     */
    @Override
    public FloatFilter copy() {
        return new FloatFilter(this);
    }

}
