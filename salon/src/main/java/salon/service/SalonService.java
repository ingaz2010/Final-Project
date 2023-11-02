package salon.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.PreRemove;
import salon.controller.model.CustomerData;
import salon.controller.model.EmployeeData;
import salon.controller.model.ServiceData;
import salon.dao.CustomerDao;
import salon.dao.EmployeeDao;
import salon.dao.ServiceDao;
import salon.entity.Customer;
import salon.entity.Employee;

@Service
public class SalonService {

	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private CustomerDao customerDao;
	
	@Autowired
	private ServiceDao serviceDao;
	
	// Create new Employee
	@Transactional(readOnly = false)
	public EmployeeData saveEmployee(EmployeeData employeeData) {
		Long employeeId = employeeData.getEmployeeId();     // retrieve employeeId
		Employee employee = findOrCreateEmployee(employeeId); //search employee by id, create new if not found
		
		copyEmployeeFields(employee, employeeData);     //copy info to employee 
		
		return new EmployeeData(employeeDao.save(employee));
	}

	//find employee by id, create new if not found
	private Employee findOrCreateEmployee(Long employeeId) {
		Employee employee;
		if(Objects.isNull(employeeId)) { //if id is null: create new employee
			employee = new Employee();
		} else {
			employee = findEmployeeById(employeeId); //else: find employee by id
		}
		
		return employee;
	}

	//find employee by id; throws exception if not found
	private Employee findEmployeeById(Long employeeId) {
		return employeeDao.findById(employeeId)
				.orElseThrow(() -> new NoSuchElementException("Employee with ID=" + employeeId + "was not found"));
	}

	//copy info from employeeData to employee
	private void copyEmployeeFields(Employee employee, EmployeeData employeeData) {
		employee.setEmployeeId(employeeData.getEmployeeId());
		employee.setEmployeeFirstName(employeeData.getEmployeeFirstName());
		employee.setEmployeeLastName(employeeData.getEmployeeLastName());
		employee.setEmployeePhone(employeeData.getEmployeePhone());
		employee.setEmployeePosition(employeeData.getEmployeePosition());
		
	}

	// List all employees
	@Transactional(readOnly = true)
	public List<EmployeeData> retrieveAllEmployees() {
		List<Employee> employees = employeeDao.findAll();  // list of employees found in Dao
		List<EmployeeData> emplData = new LinkedList<>();  // empty list, needs to be filled
		// filling list with EmployeeData, copy every employee using for loop
		for(Employee employee : employees) {
			emplData.add(new EmployeeData(employee));
		}
		
		return emplData;
	}

	// retrieve employee by id
	@Transactional(readOnly = true)
	public EmployeeData retrieveEmployeeById(Long employeeId) {
		Employee employee = findEmployeeById(employeeId);
		return new EmployeeData(employee);
	}

	//delete employee by id
	@Transactional(readOnly = false)
	public void deleteEmployeeById(Long employeeId) {
		Employee employee = findEmployeeById(employeeId); //find employee to delete
		employeeDao.delete(employee);   //delete
		
	}

	//CUSTOMER
	// create a customer. When we create a customer, we connect it with an employee, considering that this customer
	// is that employee's client
	@Transactional(readOnly = false)
	public CustomerData saveCustomer(Long employeeId, CustomerData customerData) {
		Employee employee = findEmployeeById(employeeId);  //find employee that will be associated with the customer
		Long customerId = customerData.getCustomerId();    //retrieve customer id
		Customer customer = findOrCreateCustomer(customerId, employeeId); //find customer, create new if not found
		
		copyCustomerFields(customer, customerData);  //copy info from customerData to customer
		
		customer.setEmployee(employee);  //set employee
		employee.getCustomers().add(customer);  //add customer to the employee's set of customers
		
		Customer dbCustomer = customerDao.save(customer); //save
		
		return new CustomerData(dbCustomer);
	}

	//copy info from customerData to customer
	private void copyCustomerFields(Customer customer, CustomerData customerData) {
		customer.setCustomerId(customerData.getCustomerId());
		customer.setCustomerFirstName(customerData.getCustomerFirstName());
		customer.setCustomerLastName(customerData.getCustomerLastName());
		customer.setCustomerPhone(customerData.getCustomerPhone());
		customer.setCustomerColorFormula(customerData.getCustomerColorFormula());
		customer.setNotes(customerData.getNotes());
		
	}

	//find customer by id, or create new if not found
	private Customer findOrCreateCustomer(Long customerId, Long employeeId) {
		Customer customer;
		if(Objects.isNull(customerId)) {
			customer = new Customer();
		} else {
			customer = findCustomerById(customerId, employeeId);
		}
		return customer;
	}

	// find customer by customer id and employeeId; throws exception if not found
	public Customer findCustomerById(Long customerId, Long employeeId) {
		Customer customer =  customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer with ID=" + customerId + " was not found"));
		if(customer.getEmployee().getEmployeeId() == employeeId) {
			return customer;
		}else {
			throw new IllegalArgumentException("Employee with ID=" + customer.getEmployee().getEmployeeId() + " does not exist");
		}
	}

	// List all customers
	@Transactional(readOnly = true)
	public List<CustomerData> retrieveAllCustomers() {
		List<Customer> customers = customerDao.findAll(); //find all customers
		List<CustomerData> customData = new LinkedList<>();
		//transfer info from customers to empty List of CustomerData
		for(Customer customer : customers) {
			customData.add(new CustomerData(customer));
		}
		return customData;
	}

	//get customer by id
	@Transactional(readOnly = true)
	public CustomerData retrieveCustomerById(Long customerId, Long employeeId) {
		Customer customer = findCustomerById(customerId, employeeId);
		return new CustomerData(customer);
	}

	//delete customer by id, also removes services that were performed for the customer
	@Transactional(readOnly = false)
	public void deleteCustomerById(Long customerId, Long employeeId) {
		Customer customer = findCustomerById(customerId, employeeId); //find customer
		removeCustomerAssociations(customerId, employeeId);
		
		customerDao.delete(customer); // delete
	}
	
	//removes customer's connections, such as employee and services
	@PreRemove
	public void removeCustomerAssociations(Long customerId, Long employeeId) {
		Customer customer = findCustomerById(customerId, employeeId);
		customer.setEmployee(null);
		for(salon.entity.Service service : customer.getServices()) {
			Long serviceId = service.getServiceId();
			deleteServiceById(customerId, serviceId);
			
		}
	}
	

	
	//SERVICE
	//create service; we do not restrict service to be performed only by employee associated with customer. 
	//customer can get services from different employees, that's why employee id may be any existing employee, not 
	// only the one associated with customer.
	@Transactional(readOnly = false)
	public ServiceData saveService(Long employeeId, Long customerId, ServiceData serviceData) {
		//find customer by id; but we do not check employee, because service could be performed by different employee
		Customer customer = customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer with ID=" + customerId + " was not found"));
		//Employee employee = customer.getEmployee(); //use this if the service is restricted only to 1 employee 
		Employee employee = findEmployeeById(employeeId); //find employee who performs the service
		Long serviceId = serviceData.getServiceId();
		
		salon.entity.Service service = findOrCreateService(customerId, serviceId);
		copyServiceFields(service, serviceData);
		
		service.setCustomer(customer); // connect service with customer 
		customer.getServices().add(service); //add service to customer's list of services
		employee.getServices().add(service); //add service to employees list of services
		service.getEmployees().add(employee); //add employee to the set of employees in service
		
		salon.entity.Service dbService = serviceDao.save(service);
		return new ServiceData(dbService);
		
	}

	//copy info from serviceData to service
	private void copyServiceFields(salon.entity.Service service, ServiceData serviceData) {
		service.setServiceId(serviceData.getServiceId());
		service.setServiceName(serviceData.getServiceName());
		service.setServiceDate(serviceData.getServiceDate());
		service.setServicePrice(serviceData.getServicePrice());
		
	}

	//find service by id, or create new if not found
	private salon.entity.Service findOrCreateService(Long customerId, Long serviceId) {
		salon.entity.Service service;
		if(Objects.isNull(serviceId)) {
			service = new salon.entity.Service();
		} else {
			service = findServiceById(serviceId, customerId);
		}
		return service;
	}
	
	// find service by id; throws exception
	@Transactional(readOnly = true)
	public salon.entity.Service findServiceById(Long customerId, Long serviceId) {
		salon.entity.Service service = serviceDao.findById(serviceId).orElseThrow(
				() -> new NoSuchElementException("Service with ID=" + serviceId + " was not found"));
		
		if(service.getCustomer().getCustomerId() == customerId) {
			return service;
		} else {
			throw new IllegalArgumentException("Customer with ID=" + customerId + "does not exists and service with ID=" + serviceId + " could not be associated with it");
			
		}
	}

	// Get the List of all services with all data
	public List<ServiceData> retrieveAllServicesWithData() {
		List<salon.entity.Service> services = serviceDao.findAll();
		List<ServiceData> servicesData = new LinkedList<>();
		
		for(salon.entity.Service service : services) {
			ServiceData servData = new ServiceData(service);
			servData.getSalonCustomer();
			servData.getEmployees();
						
			servicesData.add(servData);
		}
		return servicesData;
	}

	// get the List of services for a particular customer
	@Transactional(readOnly = true)
	public List<ServiceData> retrieveAllServicesForCustomer(Long customerId) {
		List<salon.entity.Service> services = serviceDao.findAll(); //the list of all services
		List<ServiceData> servicesData = new LinkedList<>();
		//fill up the empty list: using for loop check every service, looking for particular customer's services
		for(salon.entity.Service service : services) {
			if(service.getCustomer().getCustomerId() == customerId) {
				ServiceData servData = new ServiceData(service);
				servData.getEmployees();
				servicesData.add(servData);
			}
		}
		return servicesData;
	}

	// get service by id
	@Transactional(readOnly = true)
	public ServiceData retrieveServiceById(Long customerId, Long serviceId) {
		salon.entity.Service service = findServiceById(customerId, serviceId);
		return new ServiceData(service);
	}

	// get the list of services that were performed by particular employee
	@Transactional(readOnly = true)
	public List<ServiceData> retrieveAllServicesForEmployee(Long employeeId) {
		List<salon.entity.Service> services = serviceDao.findAll(); //get the list of all services
		List<ServiceData> servicesData = new LinkedList<>();
		//using for loop check every service for matching employee id, add to an empty list
		for(salon.entity.Service service : services) {
			for( Employee employee : service.getEmployees()) { 
					if(employee.getEmployeeId() == employeeId) {
						ServiceData servData = new ServiceData(service);
						servData.getSalonCustomer();
						servicesData.add(servData);
			}
		}
		}
		return servicesData;
	}

	//Many to many relationship: one service can be performed by few employees (one does the color, another dries the hair, etc.)
	//add employee to Service. employee will be added to set of employees in service
	@Transactional(readOnly = false)
	public ServiceData addEmployeeToService(Long serviceId, Long customerId, EmployeeData employeeData) {
		salon.entity.Service service = findServiceById(customerId, serviceId);
		Long employeeId = employeeData.getEmployeeId(); 
		Employee employee = findEmployeeById(employeeId);//get employee by id
		service.getEmployees().add(employee); //add employee to the set in service
		employee.getServices().add(service); //add service to list of services in employee
		
		salon.entity.Service dbService = serviceDao.save(service);
		return new ServiceData(dbService);
	}

	//delete service by id
	@Transactional(readOnly = false)
	public void deleteServiceById(Long customerId, Long serviceId) {
		salon.entity.Service service = findServiceById(customerId, serviceId);
		removeServiceAssosiations(customerId, serviceId);
		serviceDao.delete(service);
	}
	
	//remove all connections from service, such as customer and employee
	@PreRemove
	public void removeServiceAssosiations(Long customerId, Long serviceId) {
		salon.entity.Service service = findServiceById(customerId, serviceId);
		service.setCustomer(null);
		for(Employee employee : service.getEmployees()) {
			employee.getServices().remove(service);
		}
	}
}