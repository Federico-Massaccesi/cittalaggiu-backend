package it.cittalaggiu.gestioneprodotti.guestFee;

import it.cittalaggiu.gestioneprodotti.monthlyExpense.MonthlyExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestsFeeRepository extends JpaRepository<GuestsFee,Long> {

    List<GuestsFee> findByAssociationId(Long associationId);

}
