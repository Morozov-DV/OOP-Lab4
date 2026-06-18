package com.example.employees;

import com.example.employees.entity.Employee;
import com.example.employees.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository repository;

    @BeforeEach
    void prepareData() {
        repository.deleteAll();
        repository.saveAll(List.of(
                employee("Анна", "Коваль", "Developer", "30000.00"),
                employee("Богдан", "Левченко", "Developer", "45000.00"),
                employee("Віра", "Марченко", "Developer", "65000.00"),
                employee("Ганна", "Олійник", "Accountant", "40000.00"),
                employee("Денис", "Поліщук", "Manager", "80000.00"),
                employee("Єва", "Савченко", "QA", "35000.00")
        ));
    }

    /** Перший тест: сторінка містить не більше двох записів. */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void paginationReturnsLimitedNumberOfRecords() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(6));
    }

    /** Другий тест: повертаються лише працівники із заданою посадою. */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void positionFilterReturnsOnlyMatchingEmployees() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("position", "Developer")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[*].position", everyItem(is("Developer"))));
    }

    /** Третій тест: одночасно застосовуються посада та діапазон зарплати. */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void combinedFilteringUsesSeveralParameters() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("position", "Developer")
                        .param("minSalary", "40000")
                        .param("maxSalary", "60000")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].firstName").value("Богдан"))
                .andExpect(jsonPath("$.content[0].position").value("Developer"))
                .andExpect(jsonPath("$.content[0].baseSalary").value(45000.00));
    }

    /**
     * Четвертий тест: від'ємна сторінка та нульовий розмір
     * не спричиняють помилку, а замінюються безпечними значеннями.
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void incorrectPaginationParametersAreNormalized() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .param("page", "-5")
                        .param("size", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.content.length()", lessThanOrEqualTo(10)));
    }

    /** П'ятий тест: без автентифікації endpoint повертає 401. */
    @Test
    void endpointIsAvailableOnlyToAuthenticatedUsers() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/employees")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk());
    }

    private Employee employee(String firstName, String lastName, String position, String salary) {
        return new Employee(firstName, lastName, position, new BigDecimal(salary));
    }
}
