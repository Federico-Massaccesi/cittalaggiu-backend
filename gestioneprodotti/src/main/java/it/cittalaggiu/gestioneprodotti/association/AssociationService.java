package it.cittalaggiu.gestioneprodotti.association;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.cittalaggiu.gestioneprodotti.UserEntity.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AssociationService {

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    Cloudinary cloudinary;

    public List<Association> getAllAssociations() {
        return associationRepository.findAll();
    }

    public Optional<Association> getAssociationById(Long id) {
        return associationRepository.findById(id);
    }

    public Association createAssociation(AssoCreateDTO payload) {

        var admin = userRepository.findById(payload.getAdminId());

        if(admin.isPresent()) {

            Association association = Association.builder()
                    .withAdmin(admin.get())
                    .withName(payload.getName())
                    .build();

            return associationRepository.save(association);
        }else{
            throw new RuntimeException("Utente non trovato");
        }
    }

    public void deleteAssociation(Long id) {
        associationRepository.deleteById(id);
    }

    public void addExpense(Long associationId, Double totalExpense) throws ResourceNotFoundException {
        Association association = associationRepository.findById(associationId)
                .orElseThrow(() -> new ResourceNotFoundException("Association not found"));

        double updatedTotalExpenses = association.getTotalExpenses() + totalExpense;
        association.setTotalExpenses(updatedTotalExpenses);

        associationRepository.save(association);
    }

    public void addIncome(Long associationId, Double income) throws ResourceNotFoundException {
        Association association = associationRepository.findById(associationId)
                .orElseThrow(() -> new ResourceNotFoundException("Association not found"));

        double updatedTotalIncome = association.getTotalIncome() + income;
        association.setTotalIncome(updatedTotalIncome);

        associationRepository.save(association);
    }

}

