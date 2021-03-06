package net.code.java;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MailNotificationRepository extends JpaRepository<MAIL, Integer> {
	
	@Query(value="Select * from mail t where t.state = 'success' order by t.id ASC LIMIT 1 ", nativeQuery = true)
    public MAIL findFirstID();

}
