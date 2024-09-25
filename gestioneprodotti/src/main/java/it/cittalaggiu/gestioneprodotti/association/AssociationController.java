package it.cittalaggiu.gestioneprodotti.association;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.cittalaggiu.gestioneprodotti.guest.Guest;
import it.cittalaggiu.gestioneprodotti.guestFee.GuestsFee;
import it.cittalaggiu.gestioneprodotti.guestFee.GuestsFeeRepository;
import it.cittalaggiu.gestioneprodotti.monthlyExpense.MonthlyExpense;
import it.cittalaggiu.gestioneprodotti.monthlyExpense.MonthlyExpenseRepository;
import it.cittalaggiu.gestioneprodotti.monthlyIncome.MonthlyIncome;
import it.cittalaggiu.gestioneprodotti.monthlyIncome.MonthlyIncomeRepository;
import it.cittalaggiu.gestioneprodotti.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/associations")
public class AssociationController {

    @Autowired
    private AssociationService associationService;

    @Autowired
    Cloudinary cloudinary;

    @Autowired
    MonthlyExpenseRepository monthlyExpenseRepository;

    @Autowired
    MonthlyIncomeRepository monthlyIncomeRepository;

    @Autowired
    GuestsFeeRepository guestsFeeRepository;

    @GetMapping
    public List<Association> getAllAssociations() {
        return associationService.getAllAssociations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Association> getAssociationById(@PathVariable Long id) {
        Optional<Association> association = associationService.getAssociationById(id);
        return association.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Association createAssociation(@RequestBody AssoCreateDTO payload) {
        return associationService.createAssociation(payload);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssociation(@PathVariable Long id) {
        var association = associationService.getAssociationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Association not found"));

        // Elimina immagini per i soci (members)
        List<Guest> members = association.getGuests();  // Assicurati che l'associazione abbia un metodo per ottenere i soci
        for (Guest member : members) {
            if (member.getImageUrl() != null && !member.getImageUrl().isEmpty()) {
                String publicId = extractPublicIdFromUrl(member.getImageUrl());
                try {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (IOException e) {
                    // Log e gestione dell'errore
                    e.printStackTrace();
                }
            }
        }

        // Elimina immagini per i prodotti (products)
        List<Product> products = association.getProducts();  // Assicurati che l'associazione abbia un metodo per ottenere i prodotti
        for (Product product : products) {
            if (product.getImageURL() != null && !product.getImageURL().isEmpty()) {
                String publicId = extractPublicIdFromUrl(product.getImageURL());
                try {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                } catch (IOException e) {
                    // Log e gestione dell'errore
                    e.printStackTrace();
                }
            }
        }

        // Elimina l'associazione dopo aver gestito le immagini
        associationService.deleteAssociation(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/addExpense")
    public ResponseEntity<?> addExpenseToAssociation(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Double> request) {
        Double totalExpense = request.get("totalExpense");
        try {
            associationService.addExpense(id, totalExpense);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Association not found");
        }
    }

    @GetMapping("/{id}/monthly-expenses")
    public ResponseEntity<List<MonthlyExpense>> getMonthlyExpenses(@PathVariable("id") Long id) {
        List<MonthlyExpense> expenses = monthlyExpenseRepository.findByAssociationId(id);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{id}/monthly-incomes")
    public ResponseEntity<List<MonthlyIncome>> getMonthlyIncomes(@PathVariable("id") Long id) {
        List<MonthlyIncome> incomes = monthlyIncomeRepository.findByAssociationId(id);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/{id}/guests-fee")
    public ResponseEntity<List<GuestsFee>> getGuestsFee(@PathVariable("id") Long id) {
        List<GuestsFee> guestsFees = guestsFeeRepository.findByAssociationId(id);
        return ResponseEntity.ok(guestsFees);
    }


    @PatchMapping("/{id}/addIncome")
    public ResponseEntity<?> addIncomeToAssociation(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Double> request) {
        Double income = request.get("income");
        try {
            associationService.addIncome(id, income);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Association not found");
        }
    }

    @PatchMapping("/{id}/monthly-fee")
    public ResponseEntity<?> updateMonthlyFee(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Double> request) {
        Double monthlyFee = request.get("monthlyFee");
        try {
            associationService.updateMonthlyFee(id, monthlyFee);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Association not found");
        }
    }


    private String extractPublicIdFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
    }
}
