package com.example.employees.config;

import com.example.employees.entity.Employee;
import com.example.employees.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeEmployees(EmployeeRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            repository.saveAll(List.of(
                    new Employee("Іван", "Петренко", "Developer", new BigDecimal("45000.00")),
                    new Employee("Олена", "Коваленко", "Developer", new BigDecimal("62000.00")),
                    new Employee("Марія", "Шевченко", "Accountant", new BigDecimal("38000.00")),
                    new Employee("Олександр", "Бондар", "Manager", new BigDecimal("70000.00")),
                    new Employee("Андрій", "Мельник", "Developer", new BigDecimal("30000.00")),
                    new Employee("Ірина", "Ткаченко", "HR", new BigDecimal("36000.00")),
                    new Employee("Дмитро", "Морозов", "Manager", new BigDecimal("55000.00")),
                    new Employee("Наталія", "Лисенко", "Accountant", new BigDecimal("42000.00")),
                    new Employee("Віктор", "Кравченко", "Developer", new BigDecimal("52000.00")),
                    new Employee("Софія", "Романенко", "QA", new BigDecimal("41000.00")),
                    new Employee("Максим", "Іванов", "QA", new BigDecimal("47000.00")),
                    new Employee("Катерина", "Сидоренко", "Developer", new BigDecimal("58000.00"))
            ));
        };
    }
}
