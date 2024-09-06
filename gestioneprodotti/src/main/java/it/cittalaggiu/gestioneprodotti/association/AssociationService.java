package it.cittalaggiu.gestioneprodotti.association;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.cittalaggiu.gestioneprodotti.UserEntity.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Association addImageToAssociation(Long id, MultipartFile file) throws IOException {
        Optional<Association> existingAssociationOpt = associationRepository.findById(id);
        if (existingAssociationOpt.isPresent()) {
            Association existingAssociation = existingAssociationOpt.get();

            var uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = uploadResult.get("url").toString();

            existingAssociation.getImagesUrl().add(url);
            return associationRepository.save(existingAssociation);
        } else {
            throw new RuntimeException("Associazione non trovata");
        }
    }
}
