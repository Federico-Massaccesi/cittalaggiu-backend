package it.cittalaggiu.gestioneprodotti.monthlyExpense;

import it.cittalaggiu.gestioneprodotti.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonthlyExpenseRepository extends JpaRepository<MonthlyExpense,Long> {
    List<MonthlyExpense> findByAssociationId(Long associationId);

}
