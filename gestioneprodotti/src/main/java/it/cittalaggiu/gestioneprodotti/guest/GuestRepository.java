package it.cittalaggiu.gestioneprodotti.guest;

import it.cittalaggiu.gestioneprodotti.association.Association;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest,Long> {

    List<Guest> findByAssociationAndMonthlyPaymentTrue(Association association);

    List<Guest> findByAssociation(Association association);

}
