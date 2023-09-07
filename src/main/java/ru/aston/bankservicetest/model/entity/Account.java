package ru.aston.bankservicetest.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account extends AbstractEntity {

    @Column(name = "number", nullable = false)
    private Long number;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "beneficiary", nullable = false)
    private Beneficiary beneficiary;

    @Column(name = "pin_code", nullable = false)
    private String pinCode;

    @Column(name = "balance", nullable = false)
    private Long balance;
}
