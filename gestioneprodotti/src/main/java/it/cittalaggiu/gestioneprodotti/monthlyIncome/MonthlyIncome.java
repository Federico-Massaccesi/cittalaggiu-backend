package it.cittalaggiu.gestioneprodotti.monthlyIncome;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.cittalaggiu.gestioneprodotti.BaseEntity;
import it.cittalaggiu.gestioneprodotti.association.Association;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "monthlyIncomes")
@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyIncome extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "association_id")
    private Association association;

    private Double totalIncome;

    private LocalDate month;
}
