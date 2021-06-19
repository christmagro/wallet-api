package com.chris.wallet.api.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerApi implements Serializable {

    private static final long serialVersionUID = -3012450543703752076L;
    @NotNull
    private UUID id;
    private String name;
    private String surname;
    @NotBlank
    @Email
    private String username;
}
