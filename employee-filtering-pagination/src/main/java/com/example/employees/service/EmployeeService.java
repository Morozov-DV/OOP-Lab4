package com.example.employees.service;

import com.example.employees.dto.EmployeeResponse;
import com.example.employees.dto.PageResponse;
import com.example.employees.entity.Employee;
import com.example.employees.repository.EmployeeRepository;
import com.example.employees.specification.EmployeeSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    // Білий список запобігає сортуванню за неіснуючим або небажаним полем.
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "firstName", "lastName", "position", "baseSalary"
    );

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public PageResponse<EmployeeResponse> findEmployees(
            String position,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            Integer page,
            Integer size,
            String sortBy,
            String direction
    ) {
        int safePage = normalizePage(page);
        int safeSize = normalizeSize(size);
        String safeSortBy = normalizeSortField(sortBy);
        Sort.Direction safeDirection = normalizeDirection(direction);

        validateSalaryRange(minSalary, maxSalary);

        Pageable pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(safeDirection, safeSortBy)
        );

        Specification<Employee> specification = Specification
                .where(EmployeeSpecifications.hasPosition(position))
                .and(EmployeeSpecifications.salaryGreaterThanOrEqual(minSalary))
                .and(EmployeeSpecifications.salaryLessThanOrEqual(maxSalary));

        Page<Employee> result = employeeRepository.findAll(specification, pageable);
        return PageResponse.fromPage(result, EmployeeResponse::fromEntity);
    }

    /** Некоректний номер сторінки замінюється нульовою сторінкою. */
    private int normalizePage(Integer page) {
        return page == null || page < 0 ? DEFAULT_PAGE : page;
    }

    /**
     * Нульовий або від'ємний розмір замінюється значенням 10,
     * а надто великий обмежується значенням 100.
     */
    private int normalizeSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private String normalizeSortField(String sortBy) {
        return sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "id";
    }

    private Sort.Direction normalizeDirection(String direction) {
        return "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    private void validateSalaryRange(BigDecimal minSalary, BigDecimal maxSalary) {
        if (minSalary != null && minSalary.signum() < 0) {
            throw new IllegalArgumentException("Мінімальна зарплата не може бути від'ємною");
        }
        if (maxSalary != null && maxSalary.signum() < 0) {
            throw new IllegalArgumentException("Максимальна зарплата не може бути від'ємною");
        }
        if (minSalary != null && maxSalary != null && minSalary.compareTo(maxSalary) > 0) {
            throw new IllegalArgumentException("Мінімальна зарплата не може перевищувати максимальну");
        }
    }
}
