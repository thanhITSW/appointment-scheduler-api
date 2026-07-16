package com.appointment.jpa.specifications.filter;

import java.util.UUID;

/**
 * Filter class for {@link UUID} type attributes.
 *
 * @see Filter
 */
public class UUIDFilter extends Filter<UUID> {

    /**
     * <p>Constructor for UUIDFilter.</p>
     */
    public UUIDFilter() {
    }

    /**
     * <p>Constructor for UUIDFilter.</p>
     *
     * @param filter a {@link UUIDFilter} object.
     */
    public UUIDFilter(final UUIDFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link UUIDFilter} object.
     */
    @Override
    public UUIDFilter copy() {
        return new UUIDFilter(this);
    }

}
