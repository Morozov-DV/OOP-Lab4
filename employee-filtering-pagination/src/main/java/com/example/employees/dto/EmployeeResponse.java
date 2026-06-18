package com.example.employees.dto;

import com.example.employees.entity.Employee;

import java.math.BigDecimal;

/**
 * DTO не передає клієнту внутрішню JPA-сутність напряму.
 */
public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String position,
        BigDecimal baseSalary
) {
    public static EmployeeResponse fromEntity(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getPosition(),
                employee.getBaseSalary()
        );
    }
}
