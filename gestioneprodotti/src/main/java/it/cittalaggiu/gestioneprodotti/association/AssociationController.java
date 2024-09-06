package it.cittalaggiu.gestioneprodotti.association;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/associations")
public class AssociationController {

    @Autowired
    private AssociationService associationService;

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
}
