package com.appointment.service.impl;

import com.appointment.service.UserQueryService;
import com.appointment.service.criteria.UserCriteria;
import com.appointment.service.dto.response.UserResponseDto;
import com.appointment.service.mapper.UserMapper;
import com.appointment.repository.UserRepository;
import com.appointment.entity.User;
import com.appointment.entity.User_;
import com.appointment.jpa.AbstractAuditingEntity_;
import com.appointment.jpa.specifications.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl extends QueryService<User> implements UserQueryService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getUsers(UserCriteria criteria, Pageable pageable) {
        Specification<User> spec = createSpecification(criteria);
        return userRepository.findAll(spec, pageable).map(userMapper::toResponseDto);
    }

    private Specification<User> createSpecification(UserCriteria criteria) {
        Specification<User> specification = (root, query, builder) -> null;
        
        if (criteria != null) {
            if (criteria.getFromDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFromDate(), AbstractAuditingEntity_.createdDate));
            }
            if (criteria.getToDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getToDate(), AbstractAuditingEntity_.createdDate));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), User_.status));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), User_.fullName));
            }
            if (criteria.getEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEmail(), User_.email));
            }
            if (criteria.getRole() != null) {
                specification = specification.and(buildSpecification(criteria.getRole(), User_.role));
            }
        }
        
        // Only add soft delete filtering (exclude deleted records)
        specification = specification.and(buildSoftDeleteFilter());
        
        return specification;
    }

    private Specification<User> buildSoftDeleteFilter() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get(User_.deleted), false);
    }
}
