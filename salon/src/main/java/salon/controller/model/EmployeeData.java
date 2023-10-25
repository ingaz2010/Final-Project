package salon.controller.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import salon.entity.Customer;
import salon.entity.Employee;
import salon.entity.Service;

@Data
@NoArgsConstructor
public class EmployeeData {

	private Long employeeId;
	private String employeeFirstName;
	private String employeeLastName;
	private String employeePhone;
	private String employeePosition;
	
	//Set of services that employee provides
	private Set<SalonService> services = new HashSet<>();
	
	//Set of customers that employee took care of
	private Set<SalonCustomer> customers = new HashSet<>();
	
	//constructor
	public EmployeeData(Employee employee) {
		employeeId = employee.getEmployeeId();
		employeeFirstName = employee.getEmployeeFirstName();
		employeeLastName = employee.getEmployeeLastName();
		employeePhone = employee.getEmployeePhone();
		employeePosition = employee.getEmployeePosition();
		
		for(Service service : employee.getServices()) {
			SalonService salonService = new SalonService(service);
			services.add(salonService);
		}
		
		for(Customer customer : employee.getCustomers()) {
			SalonCustomer salonCustomer = new SalonCustomer(customer);
			customers.add(salonCustomer);
		}
	}
	
	
	@Data
	@NoArgsConstructor
	public static class SalonService {
		private Long serviceId;
		private String serviceName;
		private String serviceDate;
		private Long servicePrice;
		
	    public SalonService (Service service) {
	    	serviceId = service.getServiceId();
	    	serviceName = service.getServiceName();
	    	serviceDate = service.getServiceDate();
	    	servicePrice = service.getServicePrice();
	    }
	}
	
	@Data
	@NoArgsConstructor
	public static class SalonCustomer{
		private Long customerId;
		private String customerFirstName;
		private String customerLastName;
		private String customerPhone;
		private String customerColorFormula;
		private String notes;
		
		public SalonCustomer(Customer customer) {
			customerId = customer.getCustomerId();
			customerFirstName = customer.getCustomerFirstName();
			customerLastName = customer.getCustomerLastName();
			customerPhone = customer.getCustomerPhone();
			customerColorFormula = customer.getCustomerColorFormula();
			notes = customer.getNotes();
		}
	}
}
