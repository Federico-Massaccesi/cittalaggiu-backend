package it.cittalaggiu.gestioneprodotti.association;


import com.cloudinary.Cloudinary;
import it.cittalaggiu.gestioneprodotti.monthlyExpense.MonthlyExpense;
import it.cittalaggiu.gestioneprodotti.monthlyExpense.MonthlyExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

}
