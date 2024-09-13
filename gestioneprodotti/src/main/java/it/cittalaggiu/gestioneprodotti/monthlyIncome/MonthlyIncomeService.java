package it.cittalaggiu.gestioneprodotti.monthlyIncome;

import it.cittalaggiu.gestioneprodotti.association.Association;
import it.cittalaggiu.gestioneprodotti.association.AssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MonthlyIncomeService {

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private MonthlyIncomeRepository monthlyIncomeRepository;

    @Scheduled(cron = "0 0 0 1 * ?") // Esegue il controllo il primo giorno di ogni mese
    public void resetMonthlyIncomes() {
        List<Association> associations = associationRepository.findAll();

        for (Association association : associations) {
            // Aggiungi una nuova entità MonthlyIncome con l'incasso del mese precedente
            MonthlyIncome monthlyIncome = new MonthlyIncome();
            monthlyIncome.setAssociation(association);
            monthlyIncome.setTotalIncome(association.getTotalIncome());  // Recupera l'incasso totale
            monthlyIncome.setMonth(LocalDate.now().minusMonths(1).withDayOfMonth(1)); // Imposta il mese

            monthlyIncomeRepository.save(monthlyIncome);

            // Resetta l'incasso totale dell'associazione
            association.setTotalIncome(0.0);
            associationRepository.save(association);
        }
    }
}
