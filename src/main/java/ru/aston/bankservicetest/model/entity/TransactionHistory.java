package ru.aston.bankservicetest.model.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.aston.bankservicetest.model.type.EOperationType;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transaction_history")
public class TransactionHistory extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "account_from")
    private Account accountFrom;

    @ManyToOne
    @JoinColumn(name = "account_to")
    private Account accountTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private EOperationType operationType;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;

    public TransactionHistory(Account accountFrom, Account accountTo, EOperationType operationType, Long amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.operationType = operationType;
        this.amount = amount;
    }

    @PrePersist
    private void onCreate() {
        this.transactionDate = new Date();
    }
}
