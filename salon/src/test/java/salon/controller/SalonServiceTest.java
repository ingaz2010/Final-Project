package salon.controller;

import java.util.function.IntPredicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import salon.controller.model.EmployeeData;
import salon.entity.Employee;

public class SalonServiceTest {
	
	private static final String EMPLOYEE_TABLE = "employee";

	//@formatter:off
	private EmployeeData insertEmployee1 = new EmployeeData(
			1L, 
			"Jennifer",
			"Smith",
			"(646) 654-3200",
			"hairdresser, manager");
	
	private EmployeeData insertEmployee2 = new EmployeeData(
			2L,
			"Lauren",
			"Johnson",
			"(347) 684-9651",
			"hairdresser, manicurist"
			);
	//@formatter:on

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private SalonController salonController;
	
	protected EmployeeData buildInsertEmployee(int which) {
		return which == 1 ? insertEmployee1 : insertEmployee2;
	}
	
	protected int rowsInEmployeeTable() {
		return JdbcTestUtils.countRowsInTable(jdbcTemplate, EMPLOYEE_TABLE);
	}

	protected EmployeeData insertEmployee(EmployeeData employeeData) {
		Employee employee = new Employee();
		employee.setEmployeeId(employeeData.getEmployeeId());
		employee.setEmployeeFirstName(employeeData.getEmployeeFirstName());
		employee.setEmployeeLastName(employeeData.getEmployeeLastName());
		employee.setEmployeePhone(employeeData.getEmployeePhone());
		employee.setEmployeePosition(employeeData.getEmployeePosition());
		
		EmployeeData clone = new EmployeeData(employee);
		clone.setEmployeeId(null);
		return salonController.insertEmployee(clone);

}
}