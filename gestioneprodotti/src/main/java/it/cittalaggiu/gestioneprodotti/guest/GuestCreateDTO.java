package it.cittalaggiu.gestioneprodotti.guest;

import it.cittalaggiu.gestioneprodotti.association.Association;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class GuestCreateDTO {

    private String name;

    private String imageUrl;

}
