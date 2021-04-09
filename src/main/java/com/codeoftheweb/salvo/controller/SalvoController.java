package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.GamePlayer;

import com.codeoftheweb.salvo.model.Salvo;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api") // Ruta
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/gamePlayers")
    public List<Object> getGamePlayers() {
        return gamePlayerRepository.findAll()
                .stream().map(GamePlayer::gamePlayerDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> gamePlayers(@PathVariable long gamePlayerId, Authentication authentication) {
        Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);

        ResponseEntity<Map<String, Object>> response;
        /* --- Si el gamePlayer está, t odo ok, sino, tira que no existe --- */
        if (gp.isPresent()) {
            response = new ResponseEntity<>(gp.get().game_view(), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(makeMap("problem", "gamePlayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        /* --- Si no está autenticado, no tiene autorización para entrar a game_view --- */
        if (!authentication.getName().equals(gp.get().getPlayer().getEmail())) {
            return new ResponseEntity<>(makeMap("problem", "player not authorized"), HttpStatus.FORBIDDEN);
        }

        Map<String, Object> dto = new LinkedHashMap<>();
        Map<String, Object> hits = new LinkedHashMap<>();
        hits.put("self", new ArrayList<>());
        hits.put("opponent", new ArrayList<>());

        dto.put("id", gp.get().getGame().getId());
        dto.put("created", gp.get().getGame().getCreatedDate());
        dto.put("gameState", "PLACESHIPS");

        dto.put("gamePlayers", gp.get().getGame().getGamePlayers()
                .stream()
                .map(gamePlayer1 -> gamePlayer1.gamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("ships", gp.get().getShips()
                .stream()
                .map(ship -> ship.shipDTO())
                .collect(Collectors.toList()));
        dto.put("salvoes", gp.get().getGame().getGamePlayers()
                .stream()
                .flatMap(gamePlayer1 -> gamePlayer1.getSalvos()
                        .stream()
                        .map(salvo -> salvo.salvoDTO()))
                .collect(Collectors.toList()));
        dto.put("hits", hits);

        return new ResponseEntity<>(dto, HttpStatus.OK);

    }

    @RequestMapping(value = "/games/players/{gamePlayerId]/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvoes (@PathVariable Long gamePlayerId, @RequestBody Salvo salvo,
                                                          Authentication authentication) {
        if (Util.isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("error", "not authorized"), HttpStatus.UNAUTHORIZED);
        }
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        if (!gamePlayer.isPresent()) {
            return new ResponseEntity<>(Util.makeMap("error", "game doesn't exist"), HttpStatus.UNAUTHORIZED);
        }

        if (!authentication.getName().equals(gamePlayer.get().getPlayer().getEmail())) {
            return new ResponseEntity<>(Util.makeMap("problem", "player not authorized"), HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.get().getSalvos().size() > 0) {
            return new ResponseEntity<>(Util.makeMap("problem", "ships already placed"), HttpStatus.FORBIDDEN);
        }

        return null;


    }




    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}

