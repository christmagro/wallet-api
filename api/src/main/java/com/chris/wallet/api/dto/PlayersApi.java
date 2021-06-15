package com.chris.wallet.api.dto;

import com.chris.wallet.api.model.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayersApi implements Serializable {
    private List<Player> players;
}
