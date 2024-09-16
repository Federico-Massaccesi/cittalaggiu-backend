package it.cittalaggiu.gestioneprodotti.association;

import it.cittalaggiu.gestioneprodotti.guest.Guest;
import it.cittalaggiu.gestioneprodotti.guest.GuestRepository;
import it.cittalaggiu.gestioneprodotti.guest.GuestService;
import it.cittalaggiu.gestioneprodotti.guestFee.GuestsFee;
import it.cittalaggiu.gestioneprodotti.guestFee.GuestsFeeRepository;
import it.cittalaggiu.gestioneprodotti.monthlyExpense.MonthlyExpense;
import it.cittalaggiu.gestioneprodotti.monthlyExpense.MonthlyExpenseRepository;
import it.cittalaggiu.gestioneprodotti.monthlyIncome.MonthlyIncome;
import it.cittalaggiu.gestioneprodotti.monthlyIncome.MonthlyIncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class FinancialService {

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private GuestsFeeRepository guestsFeeRepository;

    @Autowired
    private MonthlyExpenseRepository monthlyExpenseRepository;

    @Autowired
    private MonthlyIncomeRepository monthlyIncomeRepository;

    @Autowired
    private GuestService guestService;
    //@Scheduled(cron = "0 0 0 1 * ?") // Esegue il primo giorno di ogni mese

    @Scheduled(cron = "0 */1 * * * ?")
    public void manageMonthlyFinances() {
        List<Association> associations = associationRepository.findAll();

        // 1. Calcola le GuestsFee e aggiorna l'incasso dell'associazione
        for (Association association : associations) {
            // Trova tutti i guests dell'associazione con monthlyPayment = true
            List<Guest> guestsWithMonthlyPayment = guestRepository.findByAssociationAndMonthlyPaymentTrue(association);

            // Se ci sono guest con monthlyPayment true
            if (!guestsWithMonthlyPayment.isEmpty()) {
                // Calcola l'importo totale delle quote
                double totalIncomeFromFees = guestsWithMonthlyPayment.size() * association.getMonthlyFee();

                // Crea una nuova entità GuestsFee con l'importo calcolato
                GuestsFee guestsFee = GuestsFee.builder()
                        .withGuestsFee(guestsWithMonthlyPayment)
                        .withMonth(LocalDate.now().minusMonths(1).withDayOfMonth(1)) // Setta il mese precedente
                        .withAmount(totalIncomeFromFees)  // Setta il totale delle quote versate
                        .withAssociation(association)
                        .build();

                // Salva la nuova entità GuestsFee
                guestsFeeRepository.save(guestsFee);

                // Aggiungi l'incasso al totale dell'associazione
                association.setTotalIncome(association.getTotalIncome() + totalIncomeFromFees);

                // Salva l'associazione aggiornata
                associationRepository.save(association);
            }
        }

        // 2. Reset delle spese mensili (MonthlyExpense)
        for (Association association : associations) {
            // Crea una nuova entità MonthlyExpense con la spesa del mese precedente
            MonthlyExpense monthlyExpense = new MonthlyExpense();
            monthlyExpense.setAssociation(association);
            monthlyExpense.setTotalExpense(association.getTotalExpenses());
            monthlyExpense.setMonth(LocalDate.now().minusMonths(1).withDayOfMonth(1));

            // Salva la nuova entità MonthlyExpense
            monthlyExpenseRepository.save(monthlyExpense);

            // Resetta le spese dell'associazione
            association.setTotalExpenses(0.0);
            associationRepository.save(association);
        }

        // 3. Creazione di MonthlyIncome e reset dell'incasso totale
        for (Association association : associations) {
            // Crea una nuova entità MonthlyIncome con l'incasso del mese precedente
            MonthlyIncome monthlyIncome = new MonthlyIncome();
            monthlyIncome.setAssociation(association);
            monthlyIncome.setTotalIncome(association.getTotalIncome());  // Recupera l'incasso totale aggiornato
            monthlyIncome.setMonth(LocalDate.now().minusMonths(1).withDayOfMonth(1)); // Imposta il mese precedente

            // Salva la nuova entità MonthlyIncome
            monthlyIncomeRepository.save(monthlyIncome);

            // Resetta l'incasso totale dell'associazione
            association.setTotalIncome(0.0);
            associationRepository.save(association);
        }

        // 4. Resetta l'attributo monthlyPayment di tutti i guests a false
        for (Association association : associations) {
            List<Guest> guests = guestRepository.findByAssociation(association);

            for (Guest guest : guests) {
                // Se il guest ha il monthlyPayment settato a true, lo resettiamo a false
                if (guest.getMonthlyPayment()) {
                    guest.setMonthlyPayment(false);
                    guestService.createGuest(guest); // Salviamo il guest aggiornato
                }
            }
        }
    }

}
