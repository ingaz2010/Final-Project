package salon.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import salon.entity.Employee;

public interface EmployeeDao extends JpaRepository<Employee, Long> {

}
