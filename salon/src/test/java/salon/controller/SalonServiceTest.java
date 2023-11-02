package salon.controller;

import java.util.LinkedList;
import java.util.List;
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
	
	private EmployeeData updateEmployee1 = new EmployeeData(
			1L, 
			"Jane",
			"Defo",
			"(212) 654-3201",
			"hairdresser"
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
	
	protected EmployeeData retrieveEmployee(Long employeeId) {
		return salonController.retrieveEmployeeById(employeeId);
	}

	protected List<EmployeeData> insertTwoEmployees() {
		EmployeeData employee1 = insertEmployee(buildInsertEmployee(1));
		EmployeeData employee2 = insertEmployee(buildInsertEmployee(2));
		
		return List.of(employee1, employee2);
	}
	
	protected List<EmployeeData> retrieveAllEmployees() {
		return salonController.retrieveAllEmployees();
	}
	
	protected List<EmployeeData> sorted(List<EmployeeData> list) {
		List<EmployeeData> data = new LinkedList<>(list);
		//list.sort((empl1, empl2) -> (int)(empl1.getEmployeeId() - empl2.getEmployeeId()));
		return data;
	}
	
	protected EmployeeData updateEmployee(EmployeeData employeeData) {
		return salonController.updateEmployee(employeeData.getEmployeeId(), employeeData);
	}

	protected EmployeeData buildUpdateEmployee() {
		return updateEmployee1;
	}
	
	protected void deleteEmployee(Long employeeId) {
		salonController.deleteEmployeeById(employeeId);
		
	}
}