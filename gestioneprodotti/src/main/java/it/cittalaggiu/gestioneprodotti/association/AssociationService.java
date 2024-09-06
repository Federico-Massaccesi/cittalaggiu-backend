package it.cittalaggiu.gestioneprodotti.association;

import it.cittalaggiu.gestioneprodotti.UserEntity.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssociationService {

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private UserRepository userRepository;

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
}
