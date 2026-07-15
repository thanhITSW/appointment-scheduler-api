package com.appointment.jpa.specifications;

/**
 * Implementation should usually contain fields of Filter instances.
 */
public interface Criteria {

    /**
     * @return a new criteria with copied filters
     */
    Criteria copy();
}
