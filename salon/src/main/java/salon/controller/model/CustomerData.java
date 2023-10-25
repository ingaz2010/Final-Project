package salon.controller.model;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import salon.controller.model.EmployeeData.SalonService;
import salon.entity.Customer;
import salon.entity.Employee;
import salon.entity.Service;

@Data
@NoArgsConstructor
public class CustomerData {

	private Long customerId;
	private String customerFirstName;
	private String customerLastName;
	private String customerPhone;
	private String customerColorFormula;
	private String notes;
	private SalonEmployee employee;
	
	//List of services that has been provided for the customer
	private List<SalonService> services = new LinkedList<>();
	
	//constructor
	public CustomerData(Customer customer) {
		customerId = customer.getCustomerId();
		customerFirstName = customer.getCustomerFirstName();
		customerLastName = customer.getCustomerLastName();
		customerPhone = customer.getCustomerPhone();
		customerColorFormula = customer.getCustomerColorFormula();
		notes = customer.getNotes();
		employee = new SalonEmployee(customer.getEmployee());
		
		for(Service service : customer.getServices()) {
			SalonService salonService = new SalonService(service);
			services.add(salonService);
		}
	}
	
	@Data
	@NoArgsConstructor
	public static class SalonEmployee{
		private Long employeeId;
		private String employeeFirstName;
		private String employeeLastName;
		private String employeePhone;
		private String employeePosition;
		
		public SalonEmployee(Employee employee) {
			employeeId = employee.getEmployeeId();
			employeeFirstName = employee.getEmployeeFirstName();
			employeeLastName = employee.getEmployeeLastName();
			employeePhone = employee.getEmployeePhone();
			employeePosition = employee.getEmployeePosition();
		}
	}
}
