package it.cittalaggiu.gestioneprodotti.guest;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.cittalaggiu.gestioneprodotti.association.AssociationRepository;
import it.cittalaggiu.gestioneprodotti.security.ApiValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/guests")
public class GuestController {


    @Autowired
    private GuestService guestService;

    @Autowired
    AssociationRepository associationRepository;

    @Autowired
    Cloudinary cloudinary;

    @GetMapping
    public List<Guest> getAllGuests() {
        return guestService.getAllGuests();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        Optional<Guest> guest = guestService.getGuestById(id);
        return guest.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createGuest(@RequestPart("guest") @Validated GuestCreateDTO guestDTO,
                                         @RequestPart(value = "file", required = false) MultipartFile file,
                                         BindingResult validator,
                                         @RequestParam Long associationId) throws IOException {

        if (validator.hasErrors()) {
            throw new ApiValidationException(validator.getAllErrors());
        }

        var association = associationRepository.findById(associationId)
                .orElseThrow(() -> new ResourceNotFoundException("Association not found"));

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            var uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    com.cloudinary.utils.ObjectUtils.asMap("public_id", guestDTO.getName() + "_avatar"));
            imageUrl = uploadResult.get("url").toString();
        }

        var newGuest = Guest.builder()
                .withName(guestDTO.getName())
                .withImageUrl(imageUrl)
                .withAssociation(association)
                .build();

        guestService.createGuest(newGuest);
        return ResponseEntity.ok(newGuest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/debt")
    public ResponseEntity<Guest> updateGuestDebt(@PathVariable Long id, @RequestBody Map<String, Double> request) {
        double debt = request.get("debt");
        Guest guest = guestService.getGuestById(id).get();
        guest.setDebt(debt);
        guestService.createGuest(guest);
        return ResponseEntity.ok(guest);
    }

    @PatchMapping(path = "/{id}/image", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateGuestImage(@PathVariable Long id, @RequestPart("file") MultipartFile file) throws IOException {
        var guest = guestService.getGuestById(id).orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        if (!file.isEmpty()) {
            var uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("public_id", guest.getName() + "_photo"));
            String imageUrl = uploadResult.get("url").toString();
            guest.setImageUrl(imageUrl);
            guestService.createGuest(guest); // Aggiorna il guest nel database
        }

        return ResponseEntity.ok(guest);
    }

}
