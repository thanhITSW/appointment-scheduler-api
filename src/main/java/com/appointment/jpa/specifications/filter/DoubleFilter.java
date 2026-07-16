package com.appointment.jpa.specifications.filter;

/**
 * Filter class for {@link Double} type attributes.
 *
 * @see RangeFilter
 */
public class DoubleFilter extends RangeFilter<Double> {

    /**
     * <p>Constructor for DoubleFilter.</p>
     */
    public DoubleFilter() {
    }

    /**
     * <p>Constructor for DoubleFilter.</p>
     *
     * @param filter a {@link DoubleFilter} object.
     */
    public DoubleFilter(final DoubleFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link DoubleFilter} object.
     */
    @Override
    public DoubleFilter copy() {
        return new DoubleFilter(this);
    }

}
