package com.chris.wallet.api.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "player")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Player {

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @Column(columnDefinition = "char(36)")
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotNull
    @Email
    @Column(unique = true)
    private String username;

    @Valid
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "player")
    private List<Transaction> transactions = new ArrayList<>();


}
