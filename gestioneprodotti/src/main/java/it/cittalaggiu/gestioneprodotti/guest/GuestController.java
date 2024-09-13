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
    private AssociationRepository associationRepository;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping
    public List<GuestDTO> getAllGuests() {
        return guestService.getAllGuests().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestDTO> getGuestById(@PathVariable Long id) {
        Optional<Guest> guest = guestService.getGuestById(id);
        return guest.map(g -> ResponseEntity.ok(convertToDTO(g)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<GuestDTO> createGuest(@RequestPart("guest") GuestCreateDTO guestDTO,
                                                @RequestPart(value = "file", required = false) MultipartFile file,
                                                @RequestParam Long associationId) throws IOException {

        var association = associationRepository.findById(associationId)
                .orElseThrow(() -> new ResourceNotFoundException("Association not found"));

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            var uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("public_id", guestDTO.getName() + "_avatar"));
            imageUrl = uploadResult.get("url").toString();
        }

        var newGuest = Guest.builder()
                .withName(guestDTO.getName())
                .withImageUrl(imageUrl)
                .withAssociation(association)
                .withMonthlyPayment(false) // Imposta il valore di default
                .build();

        guestService.createGuest(newGuest);
        return ResponseEntity.ok(convertToDTO(newGuest));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/debt")
    public ResponseEntity<GuestDTO> updateGuestDebt(@PathVariable Long id, @RequestBody Map<String, Double> request) {
        double debt = request.get("debt");
        Guest guest = guestService.getGuestById(id).get();
        guest.setDebt(debt);
        guestService.createGuest(guest);
        return ResponseEntity.ok(convertToDTO(guest));
    }

    @PatchMapping("/{id}/reset")
    public ResponseEntity<GuestDTO> resetGuestDebt(@PathVariable Long id) {
        Guest guest = guestService.getGuestById(id).get();
        guest.setDebt(0.0);
        guestService.createGuest(guest);
        return ResponseEntity.ok(convertToDTO(guest));
    }

    @PatchMapping(path = "/{id}/image", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<GuestDTO> updateGuestImage(@PathVariable Long id, @RequestPart("file") MultipartFile file) throws IOException {
        var guest = guestService.getGuestById(id).orElseThrow(() -> new ResourceNotFoundException("Guest not found"));

        if (guest.getImageUrl() != null && !guest.getImageUrl().isEmpty()) {
            String publicId = guest.getImageUrl().substring(guest.getImageUrl().lastIndexOf("/") + 1,
                    guest.getImageUrl().lastIndexOf("."));
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }

        if (!file.isEmpty()) {
            var uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("public_id", guest.getName() + "_photo"));
            String imageUrl = uploadResult.get("url").toString();
            guest.setImageUrl(imageUrl);
            guestService.createGuest(guest);
        }

        return ResponseEntity.ok(convertToDTO(guest));
    }

    private GuestDTO convertToDTO(Guest guest) {
        return GuestDTO.builder()
                .withId(guest.getId())
                .withName(guest.getName())
                .withImageUrl(guest.getImageUrl())
                .withDebt(guest.getDebt())
                .withAssociationId(guest.getAssociation() != null ? guest.getAssociation().getId() : null)
                .withMonthlyPayment(guest.getMonthlyPayment())
                .build();
    }

    @PatchMapping("/{id}/monthly-payment")
    public ResponseEntity<GuestDTO> updateMonthlyPayment(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        boolean monthlyPayment = request.get("monthlyPayment");
        Guest guest = guestService.getGuestById(id).get();
        guest.setMonthlyPayment(monthlyPayment);
        guestService.createGuest(guest);
        return ResponseEntity.ok(convertToDTO(guest));
    }

}
