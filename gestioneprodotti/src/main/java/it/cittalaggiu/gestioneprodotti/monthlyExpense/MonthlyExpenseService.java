package it.cittalaggiu.gestioneprodotti.monthlyExpense;

import it.cittalaggiu.gestioneprodotti.association.Association;
import it.cittalaggiu.gestioneprodotti.association.AssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MonthlyExpenseService {
    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private MonthlyExpenseRepository monthlyExpenseRepository;

    @Scheduled(cron = "0 0 0 1 * ?") // Esegue il controllo il primo giorno di ogni mese
    public void resetMonthlyExpenses() {
        List<Association> associations = associationRepository.findAll();

        for (Association association : associations) {
            // Aggiungi una nuova entità MonthlyExpense con la spesa del mese precedente
            MonthlyExpense monthlyExpense = new MonthlyExpense();
            monthlyExpense.setAssociation(association);
            monthlyExpense.setTotalExpense(association.getTotalExpenses());
            monthlyExpense.setMonth(LocalDate.now().minusMonths(1).withDayOfMonth(1));
            monthlyExpenseRepository.save(monthlyExpense);

            association.setTotalExpenses(0.0);
            associationRepository.save(association);
        }
    }
}
