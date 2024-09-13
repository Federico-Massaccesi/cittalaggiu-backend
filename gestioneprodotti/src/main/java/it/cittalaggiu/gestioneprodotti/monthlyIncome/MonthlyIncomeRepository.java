package it.cittalaggiu.gestioneprodotti.monthlyIncome;

import it.cittalaggiu.gestioneprodotti.monthlyExpense.MonthlyExpense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlyIncomeRepository extends JpaRepository<MonthlyIncome,Long> {
    List<MonthlyIncome> findByAssociationId(Long associationId);

}
