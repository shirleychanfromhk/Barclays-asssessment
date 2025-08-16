package com.example.barclaysassessment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Schema(description = "Transaction entity representing a financial transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique transaction ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long transactionId;

    @Column(name = "accountNumber")
    @Schema(description = "Account number associated with the transaction", example = "ACC123")
    private String accountNumber;

    @Schema(description = "Type of transaction (deposit/withdraw)", example = "deposit")
    private String type;

    @Schema(description = "Amount of the transaction", example = "100.00")
    private Double amount;
}
