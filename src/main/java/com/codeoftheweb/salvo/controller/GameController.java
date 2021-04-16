package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) { // Authentication --> Spring method
        Map<String, Object> dto = new LinkedHashMap<>();

        if (!Util.isGuest(authentication)) {
            dto.put("player", playerRepository.findByEmail(authentication.getName()).playerDTO());
        } else
            dto.put("player", "Guest");
        dto.put("games", gameRepository.findAll().stream()
                .map(Game::gameDTO).collect(Collectors.toList()));

        return dto;
    }

    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;

        if (Util.isGuest(authentication)) {
            response = new ResponseEntity<>(Util.makeMap("error", "You're not allowed to enter"), HttpStatus.FORBIDDEN);
            return response;
        }
        Player player = playerRepository.findByEmail(authentication.getName());

        Game newGame = gameRepository.save(new Game(LocalDateTime.now()));

        GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(newGame, playerRepository.save(player)));

        response = new ResponseEntity<>(Util.makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);

        return response;
    }

    @PostMapping("/game/{gameId}/players")
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {

        if (Util.isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("problem", "Player not authorized."), HttpStatus.FORBIDDEN);
        }

        Optional<Game> game = gameRepository.findById(gameId);

        if (!game.isPresent()) {
            return new ResponseEntity<>(Util.makeMap("problem", "Game doesn't exist."), HttpStatus.FORBIDDEN);
        }

        if (game.get().getGamePlayers().size() == 2) {
            return new ResponseEntity<>(Util.makeMap("error", "Game is full."), HttpStatus.FORBIDDEN);
        }

        Player player = playerRepository.findByEmail(authentication.getName());
        GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(game.get(), player));

        return new ResponseEntity<>(Util.makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }
}
