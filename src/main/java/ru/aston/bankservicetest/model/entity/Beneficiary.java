package ru.aston.bankservicetest.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "beneficiary")
public class Beneficiary extends AbstractEntity {

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "beneficiary", fetch = FetchType.LAZY)
    private Set<Account> accounts;
}
