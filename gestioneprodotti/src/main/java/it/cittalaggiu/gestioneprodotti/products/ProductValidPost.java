package it.cittalaggiu.gestioneprodotti.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record ProductValidPost(
        @NotBlank
         String name,
        @NotNull
         Double salePrice,

        Double purchasePrice,

                @NotNull
        Boolean available,
        Integer quantity
) {
}
