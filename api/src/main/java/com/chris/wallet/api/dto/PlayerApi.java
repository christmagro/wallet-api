package com.chris.wallet.api.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
public class PlayerApi implements Serializable {

    @NotNull
    private UUID id;
    private String name;
    private String surname;
    @NotBlank
    @Email
    private String username;
}
