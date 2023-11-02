package salon.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import salon.SalonApplication;
import salon.controller.model.EmployeeData;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = SalonApplication.class)
@ActiveProfiles("test")
@SqlConfig(encoding = "utf-8")
class SalonControllerTest extends SalonServiceTest{

	@Test
	void testInsertEmployee() {
		//Given: an Employee 

		EmployeeData request = buildInsertEmployee(1);
		EmployeeData expected = buildInsertEmployee(1);
		
		//When: Employee is added to the location table
		EmployeeData actual= insertEmployee(request);
		
		//Then: Employee is returned as what is expected
		assertThat(actual).isEqualTo(expected);
		
		//And: there is one row in the table
		assertThat(rowsInEmployeeTable()).isOne();
	}

	@Test
	void testRetrieveEmpoyee() {
		//Given: employee
		EmployeeData employee = insertEmployee(buildInsertEmployee(1));
		EmployeeData expected = buildInsertEmployee(1);
		
		//When: employee is retrieved by ID
		EmployeeData actual = retrieveEmployee(employee.getEmployeeId());
		
		//Then: the actual employee data is equal to the expected data
		assertThat(actual).isEqualTo(expected);
		
	}

	@Test
	void testRetrieveAllEmployees() {
		//Given: two employees
		List<EmployeeData> expected = insertTwoEmployees();
		
		//When: all employees are retrieved
		List<EmployeeData> actual = retrieveAllEmployees();
		
		//Then: retrieved employees are the same as expected
		assertThat(sorted(actual)).isEqualTo(sorted(expected));
	}
	
	@Test
	void testUpdateEmployee() {
		//Given: employee and update request
		insertEmployee(buildInsertEmployee(1));
		EmployeeData expected = buildUpdateEmployee();
		
		//When: employee is updated
		EmployeeData actual = updateEmployee(expected);
		
		//Then: employee info is returned as expected
		assertThat(actual).isEqualTo(expected);
		
		//And: there is one row in the employee table
		assertThat(rowsInEmployeeTable()).isOne();
	}

	@Test
	void testDeleteEmployee() {
		//Given: employee and two services
		EmployeeData employee = insertEmployee(buildInsertEmployee(1));
		Long employeeId = employee.getEmployeeId();
		
		//When: employee is deleted
		deleteEmployee(employeeId);
		
		//Then: employee data is deleted
		assertThat(rowsInEmployeeTable()).isZero();
	}
	
	}

