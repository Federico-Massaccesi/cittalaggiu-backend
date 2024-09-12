package it.cittalaggiu.gestioneprodotti.products;

import com.cloudinary.Cloudinary;
import it.cittalaggiu.gestioneprodotti.association.AssociationRepository;
import it.cittalaggiu.gestioneprodotti.security.ApiValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    ProductService service;

    @Autowired
    Cloudinary cloudinary;

    @Autowired
    ProductRepository prodRepo;

    @Autowired
    AssociationRepository associationRepository;

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(name = "q", required = false) String query) {
        if (query == null || query.isEmpty()) {
            return List.of();
        } else {
            return service.findByNameStartingWith(query);
        }
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createProduct(
            @RequestPart("product") @Validated ProductValidPost productDTO,
            @RequestPart("file") MultipartFile file,
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
                    com.cloudinary.utils.ObjectUtils.asMap("public_id", productDTO.name() + "_avatar"));
            imageUrl = uploadResult.get("url").toString();
        }

        var newProduct = Product.builder()
                .withName(productDTO.name())
                .withPurchasePrice(productDTO.purchasePrice())  // Imposta il prezzo di acquisto
                .withSalePrice(productDTO.salePrice())          // Imposta il prezzo di vendita
                .withAvailable(productDTO.available())
                .withQuantity(productDTO.quantity())
                .withImageURL(imageUrl)
                .withAssociation(association) // Imposta l'associazione
                .build();

        service.save(newProduct);
        return ResponseEntity.ok(newProduct);
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<Product> updateProductAvailability(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request
    ) {
        boolean available = request.get("available");
        Product updatedProduct = service.updateProductAvailability(id, available);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart(value = "product", required = false) @Validated ProductValidPut product,
            @RequestPart(value = "file", required = false) MultipartFile file,
            BindingResult validator
    ) {
        try {
            if (validator.hasErrors()) {
                throw new ApiValidationException(validator.getAllErrors());
            }

            Product existingProduct = prodRepo.getById(id);
            String imageUrl = existingProduct.getImageURL();

            if (product != null) {
                if (file != null && !file.isEmpty()) {
                    var uploadResult = cloudinary.uploader().upload(file.getBytes(),
                            com.cloudinary.utils.ObjectUtils.asMap("public_id", product.name() + "_avatar"));
                    imageUrl = uploadResult.get("url").toString();

                    String publicId = extractPublicIdFromUrl(existingProduct.getImageURL());
                    cloudinary.uploader().destroy(publicId, null);

                    existingProduct.setImageURL(imageUrl);
                }

                existingProduct.setName(product.name());
                existingProduct.setAvailable(product.available());
                existingProduct.setPurchasePrice(product.purchasePrice());  // Aggiorna il prezzo di acquisto
                existingProduct.setSalePrice(product.salePrice());
            }

            Product updatedProduct = service.updateProduct(id, existingProduct);
            return ResponseEntity.ok(updatedProduct);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante il caricamento dell'immagine su Cloudinary: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String publicIdWithExtension = parts[parts.length - 1];
        return publicIdWithExtension.split("\\.")[0];
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<Product> updateProductQuantity(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request
    ) {
        Integer quantity = request.get("quantity");
        if (quantity == null || quantity < 0) {
            return ResponseEntity.badRequest().body(null);
        }

        Product updatedProduct = service.updateProductQuantity(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}/addQuantity")
    public ResponseEntity<?> addProductQuantity(
            @PathVariable("id") Long id,
            @RequestParam("quantity") Integer quantity) {
        try {
            service.addProductQuantity(id, quantity);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }
}
