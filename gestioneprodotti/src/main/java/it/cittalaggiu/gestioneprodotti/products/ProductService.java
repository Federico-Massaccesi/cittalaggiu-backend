package it.cittalaggiu.gestioneprodotti.products;

import it.cittalaggiu.gestioneprodotti.security.ApiValidationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;

    public Product save(Product product) {
        return repository.save(product);
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));
    }

    public Product modify(Long id, Product product) {
        var founded = repository.findById(id);

        if (founded.isPresent()) {
            Product modifiedProduct = new Product();
            BeanUtils.copyProperties(product, modifiedProduct);
            return repository.save(modifiedProduct);
        } else {
            throw new RuntimeException("Prodotto non trovato");
        }
    }

    @Transactional
    public Product updateProductAvailability(Long productId, boolean available) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));

        product.setAvailable(available);
        return repository.save(product);
    }

    @Transactional
    public Product updateProduct(Long productId, Product updatedProduct) {
        Product product = repository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));

        product.setName(updatedProduct.getName());
        product.setAvailable(updatedProduct.getAvailable());
        product.setImageURL(updatedProduct.getImageURL());

        return repository.save(product);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Product> findByNameStartingWith(String prefix) {
        return repository.findByNameStartingWith(prefix);
    }

    public Product updateProductQuantity(Long id, Integer quantity) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        int newQuantity = product.getQuantity() - quantity;
        if (newQuantity < 0) {
            throw new RuntimeException();
        }
        product.setQuantity(newQuantity);

        return repository.save(product);
    }

    public void addProductQuantity(Long id, Integer quantity) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setQuantity(product.getQuantity() + quantity);
        repository.save(product);
    }
}
