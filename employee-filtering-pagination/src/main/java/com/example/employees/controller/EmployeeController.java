package com.example.employees.controller;

import com.example.employees.dto.EmployeeResponse;
import com.example.employees.dto.PageResponse;
import com.example.employees.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Приклади:
     * GET /api/employees?page=0&size=5
     * GET /api/employees?position=Developer
     * GET /api/employees?position=Developer&minSalary=30000&maxSalary=60000
     */
    @GetMapping
    public PageResponse<EmployeeResponse> getEmployees(
            @RequestParam(required = false) String position,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return employeeService.findEmployees(
                position,
                minSalary,
                maxSalary,
                page,
                size,
                sortBy,
                direction
        );
    }
}
