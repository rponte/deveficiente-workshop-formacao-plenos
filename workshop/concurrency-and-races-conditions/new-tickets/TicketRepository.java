package br.com.zup.edu.raceconditions.tickets.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    public Long countByEvent(Event event);
 
}
