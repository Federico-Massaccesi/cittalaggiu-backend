package it.cittalaggiu.gestioneprodotti.products;

import com.cloudinary.Cloudinary;
import it.cittalaggiu.gestioneprodotti.security.ApiValidationException;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> save(@RequestPart("product") @Validated ProductValidPost product,
                                  @RequestPart("file") MultipartFile file, BindingResult validator) throws IOException {

        if (validator.hasErrors()) {
            throw new ApiValidationException(validator.getAllErrors());
        }

        try {
            var uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    com.cloudinary.utils.ObjectUtils.asMap("public_id", product.name() + "_avatar"));
            String url = uploadResult.get("url").toString();

            var newProduct = Product.builder()
                    .withName(product.name())
                    .withAvailable(product.available())
                    .withPrice(product.price())
                    .withImageURL(url)
                    .build();

            service.save(newProduct);
            return ResponseEntity.ok(newProduct);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image to Cloudinary");
        }
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
                existingProduct.setPrice(product.price());
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
}
