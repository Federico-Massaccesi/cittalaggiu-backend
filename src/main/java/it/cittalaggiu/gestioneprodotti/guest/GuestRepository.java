package it.cittalaggiu.gestioneprodotti.guest;

import it.cittalaggiu.gestioneprodotti.association.Association;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest,Long> {

    List<Guest> findByAssociationAndMonthlyPaymentTrue(Association association);

    List<Guest> findByAssociation(Association association);

    @Query("SELECT g FROM Guest g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :prefix, '%'))")
    List<Guest> findByNameContainingIgnoreCase(String prefix);



}
