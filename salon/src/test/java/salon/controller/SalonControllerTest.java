package salon.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
//@Sql//(scripts = {"classpath:schema.sql"})
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


	}

