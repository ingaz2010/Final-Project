package salon.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import salon.entity.Service;

public interface ServiceDao extends JpaRepository<Service, Long> {

	
}
