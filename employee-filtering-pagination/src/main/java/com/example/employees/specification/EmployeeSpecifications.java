package com.example.employees.specification;

import com.example.employees.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Набір специфікацій для формування динамічного SQL-запиту.
 */
public final class EmployeeSpecifications {

    private EmployeeSpecifications() {
    }

    public static Specification<Employee> hasPosition(String position) {
        return (root, query, criteriaBuilder) -> {
            if (position == null || position.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            // Порівняння без урахування регістру символів.
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("position")),
                    position.trim().toLowerCase(Locale.ROOT)
            );
        };
    }

    public static Specification<Employee> salaryGreaterThanOrEqual(BigDecimal minSalary) {
        return (root, query, criteriaBuilder) -> minSalary == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.greaterThanOrEqualTo(root.get("baseSalary"), minSalary);
    }

    public static Specification<Employee> salaryLessThanOrEqual(BigDecimal maxSalary) {
        return (root, query, criteriaBuilder) -> maxSalary == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.lessThanOrEqualTo(root.get("baseSalary"), maxSalary);
    }
}
