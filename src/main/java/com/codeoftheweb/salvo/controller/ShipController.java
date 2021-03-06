package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Ship;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ShipController {

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @PostMapping("/games/players/{gamePlayerId}/ships")
    public ResponseEntity<Map<String, Object>> addShips(@PathVariable Long gamePlayerId,
                                                        @RequestBody Set<Ship> ships, Authentication authentication) {
        if (Util.isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("problem", "No user logged!"), HttpStatus.UNAUTHORIZED);
        }

        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if (!gamePlayer.isPresent()) {
            return new ResponseEntity<>(Util.makeMap("problem", "gamePlayer doesn't exist."), HttpStatus.UNAUTHORIZED);
        }

        if (!gamePlayer.get().getPlayer().getEmail().equals(authentication.getName())) {
            return new ResponseEntity<>(Util.makeMap("problem", "Player not authorized."), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.get().getShips().size() > 0) {
            return new ResponseEntity<>(Util.makeMap("problem", "Ships already placed!"), HttpStatus.FORBIDDEN);
        }

        ships.stream().forEach(s -> {
            s.setGamePlayer(gamePlayer.get());
            shipRepository.save(s);
        });
        gamePlayerRepository.save(gamePlayer.get());

        return new ResponseEntity<>(Util.makeMap("OK", "SHIPS CREATED!"), HttpStatus.CREATED);
    }
}
