package it.cittalaggiu.gestioneprodotti.association;

import lombok.Data;

@Data
public class AssoCreateDTO {

    private String name;

    private Long adminId;

    private double monthlyFee;
}
