package salon.service;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Transactional(readOnly = false)
	public EmployeeData saveEmployee(EmployeeData employeeData) {
		Long employeeId = employeeData.getEmployeeId();
		Employee employee = findOrCreateEmployee(employeeId);
		//Set<salon.entity.Service> services = serviceDao.findAllByServiceIn(employeeData.getServices());
		
		copyEmployeeFields(employee, employeeData);
		
		/*for(salon.entity.Service service : services) {
			employee.getServices().add(service);
		}
		for(salon.entity.Service service : employeeData.getServices()) {
			employee.getServices().add(service);
		}*/
		
		return new EmployeeData(employeeDao.save(employee));
	}

	private Employee findOrCreateEmployee(Long employeeId) {
		Employee employee;
		if(Objects.isNull(employeeId)) {
			employee = new Employee();
		} else {
			employee = findEmployeeById(employeeId);
		}
		
		return employee;
	}

	private Employee findEmployeeById(Long employeeId) {
		return employeeDao.findById(employeeId)
				.orElseThrow(() -> new NoSuchElementException("Employee with ID=" + employeeId + "was not found"));
	}

	private void copyEmployeeFields(Employee employee, EmployeeData employeeData) {
		employee.setEmployeeId(employeeData.getEmployeeId());
		employee.setEmployeeFirstName(employeeData.getEmployeeFirstName());
		employee.setEmployeeLastName(employeeData.getEmployeeLastName());
		employee.setEmployeePhone(employeeData.getEmployeePhone());
		employee.setEmployeePosition(employeeData.getEmployeePosition());
		
	}

	@Transactional(readOnly = true)
	public List<EmployeeData> retrieveAllEmployees() {
		List<Employee> employees = employeeDao.findAll();
		List<EmployeeData> emplData = new LinkedList<>();
		
		for(Employee employee : employees) {
			emplData.add(new EmployeeData(employee));
		}
		
		return emplData;
	}

	@Transactional(readOnly = true)
	public EmployeeData retrieveEmployeeById(Long employeeId) {
		Employee employee = findEmployeeById(employeeId);
		return new EmployeeData(employee);
	}

	@Transactional(readOnly = false)
	public void deleteEmployeeById(Long employeeId) {
		Employee employee = findEmployeeById(employeeId);
		employeeDao.delete(employee);
		
	}

	
	@Transactional(readOnly = false)
	public CustomerData saveCustomer(Long employeeId, CustomerData customerData) {
		Employee employee = findEmployeeById(employeeId);
		Long customerId = customerData.getCustomerId();
		Customer customer = findOrCreateCustomer(customerId, employeeId);
		
		copyCustomerFields(customer, customerData);
		
		customer.setEmployee(employee);
		employee.getCustomers().add(customer);
		
		Customer dbCustomer = customerDao.save(customer);
		
		return new CustomerData(dbCustomer);
	}

	private void copyCustomerFields(Customer customer, CustomerData customerData) {
		customer.setCustomerId(customerData.getCustomerId());
		customer.setCustomerFirstName(customerData.getCustomerFirstName());
		customer.setCustomerLastName(customerData.getCustomerLastName());
		customer.setCustomerPhone(customerData.getCustomerPhone());
		customer.setCustomerColorFormula(customerData.getCustomerColorFormula());
		customer.setNotes(customerData.getNotes());
		
	}

	private Customer findOrCreateCustomer(Long customerId, Long employeeId) {
		Customer customer;
		if(Objects.isNull(customerId)) {
			customer = new Customer();
		} else {
			customer = findCustomerById(customerId, employeeId);
		}
		return customer;
	}

	public Customer findCustomerById(Long customerId, Long employeeId) {
		Customer customer =  customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer with ID=" + customerId + " was not found"));
		if(customer.getEmployee().getEmployeeId() == employeeId) {
			return customer;
		}else {
			throw new IllegalArgumentException("Employee with ID=" + customer.getEmployee().getEmployeeId() + " does not exist");
		}
	}

	@Transactional(readOnly = true)
	public List<CustomerData> retrieveAllCustomers() {
		List<Customer> customers = customerDao.findAll();
		List<CustomerData> customData = new LinkedList<>();
		
		for(Customer customer : customers) {
			customData.add(new CustomerData(customer));
		}
		return customData;
	}

	@Transactional(readOnly = true)
	public CustomerData retrieveCustomerById(Long customerId, Long employeeId) {
		Customer customer = findCustomerById(customerId, employeeId);
		return new CustomerData(customer);
	}

	@Transactional(readOnly = false)
	public void deleteCustomerById(Long customerId, Long employeeId) {
		Customer customer = findCustomerById(customerId, employeeId);
		customerDao.delete(customer);
	}

	
	@Transactional(readOnly = false)
	public ServiceData saveService(Long employeeId, Long customerId, ServiceData serviceData) {
		Customer customer = customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer with ID=" + customerId + " was not found"));
		//Employee employee = customer.getEmployee(); //use this if the service is restricted only to 1 employee 
		Employee employee = findEmployeeById(employeeId);
		Long serviceId = serviceData.getServiceId();
		
		salon.entity.Service service = findOrCreateService(customerId, serviceId);
		copyServiceFields(service, serviceData);
		
		service.setCustomer(customer);
		customer.getServices().add(service);
		employee.getServices().add(service);
		service.getEmployees().add(employee);
		
		salon.entity.Service dbService = serviceDao.save(service);
		return new ServiceData(dbService);
		
	}

	private void copyServiceFields(salon.entity.Service service, ServiceData serviceData) {
		service.setServiceId(serviceData.getServiceId());
		service.setServiceName(serviceData.getServiceName());
		service.setServiceDate(serviceData.getServiceDate());
		service.setServicePrice(serviceData.getServicePrice());
		
	}

	private salon.entity.Service findOrCreateService(Long customerId, Long serviceId) {
		salon.entity.Service service;
		if(Objects.isNull(serviceId)) {
			service = new salon.entity.Service();
		} else {
			service = findServiceById(serviceId, customerId);
		}
		return service;
	}
	
	public salon.entity.Service findServiceById(Long serviceId, Long customerId) {
		salon.entity.Service service = serviceDao.findById(serviceId).orElseThrow(
				() -> new NoSuchElementException("Service with ID=" + serviceId + " was not found"));
		
		if(service.getCustomer().getCustomerId() == customerId) {
			return service;
		} else {
			throw new IllegalArgumentException("Customer with ID=" + customerId + "does not exists and service could not be associated with it");
			
		}
	}

	@Transactional(readOnly = true)
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

	@Transactional(readOnly = true)
	public List<ServiceData> retrieveAllServicesForCustomer(Long customerId) {
		List<salon.entity.Service> services = serviceDao.findAll();
		List<ServiceData> servicesData = new LinkedList<>();
		for(salon.entity.Service service : services) {
			if(service.getCustomer().getCustomerId() == customerId) {
				ServiceData servData = new ServiceData(service);
				servData.getEmployees();
				servicesData.add(servData);
			}
		}
		return servicesData;
	}

	@Transactional(readOnly = true)
	public ServiceData retrieveServiceById(Long customerId, Long serviceId) {
		salon.entity.Service service = findServiceById(serviceId, customerId);
		return new ServiceData(service);
	}

	@Transactional(readOnly = true)
	public List<ServiceData> retrieveAllServicesForEmployee(Long employeeId) {
		List<salon.entity.Service> services = serviceDao.findAll();
		List<ServiceData> servicesData = new LinkedList<>();
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

	@Transactional(readOnly = false)
	public ServiceData addEmployeeToService(Long serviceId, Long customerId, EmployeeData employeeData) {
		salon.entity.Service service = findServiceById(customerId, serviceId);
		Long employeeId = employeeData.getEmployeeId();
		Employee employee = findEmployeeById(employeeId);
		service.getEmployees().add(employee);
		employee.getServices().add(service);
		
		salon.entity.Service dbService = serviceDao.save(service);
		return new ServiceData(dbService);
	}
}