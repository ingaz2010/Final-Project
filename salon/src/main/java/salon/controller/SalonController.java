package salon.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import salon.controller.model.CustomerData;
import salon.controller.model.EmployeeData;
import salon.controller.model.ServiceData;
import salon.service.SalonService;

@RestController
@RequestMapping("/salon")
@Slf4j
public class SalonController {
	
	@Autowired
	private SalonService salonService;
	
	//CRUD operations for Employee
	
	@PostMapping("/employee")
	@ResponseStatus(code = HttpStatus.CREATED)
	public EmployeeData insertEmployee(@RequestBody EmployeeData employeeData) {
		log.info("Created Employee {}", employeeData);
		return salonService.saveEmployee(employeeData);
	}
	
	@GetMapping("/employee")
	public List<EmployeeData> retrieveAllEmployees(){
		log.info("Retrieving all Employees");
		return salonService.retrieveAllEmployees();
	}
	
	@GetMapping("/employee/{employeeId}")
	public EmployeeData retrieveEmployeeById(@PathVariable Long employeeId) {
		log.info("Retrieving employee with ID={}", employeeId);
		return salonService.retrieveEmployeeById(employeeId);
	}
	
	@PutMapping("/employee/{employeeId}")
	public EmployeeData updateEmployee(@PathVariable Long employeeId, @RequestBody EmployeeData employeeData) {
		employeeData.setEmployeeId(employeeId);
		log.info("Updating Employee with ID={}", employeeId);
		return salonService.saveEmployee(employeeData);
	}
	
	@DeleteMapping("/employee/{employeeId}")
	public Map<String, String> deleteEmployeeById(@PathVariable Long employeeId){
		log.info("Deleting Employee with ID={}", employeeId);
		salonService.deleteEmployeeById(employeeId);
		return Map.of("Message", "Deetion of employee with ID=" + employeeId + "was successful");
	}
	
	@DeleteMapping("/employee")
	public void deleteAllEmployees() {
		log.info("You are trying to delete all employees");
		throw new UnsupportedOperationException("You are not allowed to delete all employees");
	}
	
	//CRUD operations for Customer
	@PostMapping("/{employeeId}/customer")
	@ResponseStatus(code = HttpStatus.CREATED)
	public CustomerData insertCustomer(@PathVariable Long employeeId, @RequestBody CustomerData customerData) {
		log.info("Created Customer {}", customerData);
		return salonService.saveCustomer(employeeId, customerData);
	}
	
	@GetMapping("/customer")
	public List<CustomerData> retrieveAllCustomers() {
		log.info("Retrieving all Customers");
		return salonService.retrieveAllCustomers();
	}
	
	@GetMapping("/{employeeId}/customer/{customerId}")
	public CustomerData retrieveCustomerById(@PathVariable Long customerId, @PathVariable Long employeeId) {
		log.info("Retrieving customer with ID={}", customerId);
		return salonService.retrieveCustomerById(customerId, employeeId);
	}
	
	@PutMapping("{employeeId}/customer/{customerId}")
	public CustomerData updateCustomer(@PathVariable Long employeeId, @PathVariable Long customerId, @RequestBody CustomerData customerData ) {
		customerData.setCustomerId(customerId);
		log.info("Updating customer with ID={}", customerId);
		return salonService.saveCustomer(customerId, customerData);
	}
	
	@DeleteMapping("{employeeId}/customer/{customerId}")
	public Map<String, String> deleteCustomerById(@PathVariable Long employeeId, @PathVariable Long customerId){
		log.info("Deleting customer with ID={}", customerId);
		salonService.deleteCustomerById(customerId, employeeId);
		return Map.of("message", "Deletion of customer with ID=" + customerId + " was suuccessful");
	}
	
	@DeleteMapping("/customer")
	public void deleteAllCustomers() {
		log.info("You are trying to delete all customers");
		throw new UnsupportedOperationException("You are not allowed to delete all customers");
	}
	
	
	//CRUD operations for Service
	@PostMapping("/{employeeId}/{customerId}/service")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ServiceData addService(@PathVariable Long employeeId, @PathVariable Long customerId, @RequestBody ServiceData serviceData) {
		log.info("Created Service {}", serviceData);
		return salonService.saveService(employeeId, customerId, serviceData);
	}
	
	@GetMapping("/service")
	public List<ServiceData> retrieveAllServicesWithData() {
		log.info("Retrieving All Services with All Information");
		return salonService.retrieveAllServicesWithData();
	}
	
	@GetMapping("/c/{customerId}/service")
	public List<ServiceData> retrieveAllServicesForCustomer(@PathVariable Long customerId) {
		log.info("Retrieving All Services for customer with ID={}", customerId);
		return salonService.retrieveAllServicesForCustomer(customerId);
	}
	
	@GetMapping("/em/{employeeId}/service")
	public List<ServiceData> retrieveAllServicesForEmployee(@PathVariable Long employeeId){
		log.info("retrieving All Services for employee with ID={}", employeeId);
		return salonService.retrieveAllServicesForEmployee(employeeId);
	}
	
	@GetMapping("/{customerId}/service/{serviceId}")
	public ServiceData retrieveServiceById(@PathVariable Long customerId, @PathVariable Long serviceId){
		log.info("Retrieving service with ID={}", serviceId);
		return salonService.retrieveServiceById(customerId, serviceId);
	}
	
	@PutMapping("/{customerId}/service/{serviceId}")
	public ServiceData updateServiceAddEmployee(@PathVariable Long customerId, @PathVariable Long serviceId,
			@RequestBody EmployeeData employeeData){
		log.info("Updating service with ID={}", serviceId);
		return salonService.addEmployeeToService(serviceId, customerId, employeeData);
	}
	
	@DeleteMapping("/service")
	public void deleteAllServices() {
		log.info("You are trying to delete all services");
		throw new UnsupportedOperationException("You are not allowed to delete services");
	}
	
	@DeleteMapping("/{customerId}/service/{serviceId}")
	public void deleteServiceById(@PathVariable Long customerId, @PathVariable Long serviceId) {
		log.info("You are trying to delete service with ID={}", serviceId);
		throw new UnsupportedOperationException("You are not allowed to delete any services");
	}
	}
