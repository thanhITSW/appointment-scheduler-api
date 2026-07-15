package com.appointment.jpa.specifications.filter;

/**
 * Filter class for {@link Integer} type attributes.
 *
 * @see RangeFilter
 */
public class IntegerFilter extends RangeFilter<Integer> {

    /**
     * <p>Constructor for IntegerFilter.</p>
     */
    public IntegerFilter() {
    }

    /**
     * <p>Constructor for IntegerFilter.</p>
     *
     * @param filter a {@link IntegerFilter} object.
     */
    public IntegerFilter(final IntegerFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link IntegerFilter} object.
     */
    @Override
    public IntegerFilter copy() {
        return new IntegerFilter(this);
    }

}
