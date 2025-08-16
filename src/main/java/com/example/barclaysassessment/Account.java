package com.example.barclaysassessment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Schema(description = "Account entity representing a bank account")
public class Account {
    @Id
    @NonNull
    @Schema(description = "Account number", example = "ACC123", required = true)
    private String accountNumber;

    @NonNull
    @Schema(description = "Type of account (e.g., savings, checking)", example = "savings", required = true)
    private String accountType;

    @NonNull
    @Schema(description = "Current balance", example = "1000.00", required = true)
    private double balance;

    @ManyToOne
    @JoinColumn(name = "id")
    @JsonBackReference
    @Schema(description = "User who owns this account")
    private User user;
}
