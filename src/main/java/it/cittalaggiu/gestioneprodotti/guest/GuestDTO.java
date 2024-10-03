package it.cittalaggiu.gestioneprodotti.guest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "with")
public class GuestDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Double debt;
    private Long associationId;
    private Boolean monthlyPayment;

}
