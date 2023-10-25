package salon.controller.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import salon.controller.model.CustomerData.SalonEmployee;
import salon.controller.model.EmployeeData.SalonCustomer;
import salon.entity.Employee;
import salon.entity.Service;

@Data
@NoArgsConstructor
public class ServiceData {

	private Long serviceId;
	private String serviceName;
	private String serviceDate;
	private Long servicePrice;
	
    private SalonCustomer salonCustomer;
	private Set<SalonEmployee> employees = new HashSet<>();
	
	//constructor
	public ServiceData (Service service) {
		serviceId = service.getServiceId();
		serviceName = service.getServiceName();
		serviceDate = service.getServiceDate();
		servicePrice = service.getServicePrice();
		salonCustomer = new SalonCustomer(service.getCustomer());
		
		for(Employee employee : service.getEmployees()) {
			SalonEmployee salonEmployee = new SalonEmployee(employee);
			employees.add(salonEmployee);
		}
	}
}
