package it.cittalaggiu.gestioneprodotti.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record ProductValidPut(
        String name,
        Double salePrice,
        Double purchasePrice,

                Boolean available
) {

}
