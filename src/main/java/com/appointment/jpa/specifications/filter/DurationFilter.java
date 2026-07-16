package com.appointment.jpa.specifications.filter;

import java.time.Duration;

/**
 * Filter class for {@link Duration} type attributes.
 *
 * @see Filter
 */
public class DurationFilter extends RangeFilter<Duration> {

    /**
     * <p>Constructor for DurationFilter.</p>
     */
    public DurationFilter() {
    }

    /**
     * <p>Constructor for DurationFilter.</p>
     *
     * @param filter a {@link DurationFilter} object.
     */
    public DurationFilter(final DurationFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link DurationFilter} object.
     */
    @Override
    public DurationFilter copy() {
        return new DurationFilter(this);
    }

}
