package salon.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import salon.entity.Customer;

public interface CustomerDao extends JpaRepository<Customer, Long> {

	

}
