package com.example.barclaysassessment;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Schema(description = "User entity representing a bank customer")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique user ID", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Name of the user", example = "John Doe", required = true)
    private String name;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    @Schema(description = "List of accounts owned by the user")
    private List<Account> accounts;
}
