package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.*;

import com.codeoftheweb.salvo.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @Autowired
    private SalvoRepository salvoRepository;

    @GetMapping("/gamePlayers")
    public List<Object> getGamePlayers() {
        return gamePlayerRepository.findAll()
                .stream().map(GamePlayer::gamePlayerDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> gamePlayers(@PathVariable long gamePlayerId, Authentication authentication) {
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        Player player = playerRepository.findByEmail(authentication.getName());

        ResponseEntity<Map<String, Object>> response;
        /* --- Si el gamePlayer está, t odo ok, sino, tira que no existe --- */
        if (gamePlayer.isPresent()) {
            response = new ResponseEntity<>(gamePlayer.get().game_view(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Util.makeMap("problem", "gamePlayer doesn't exist."), HttpStatus.UNAUTHORIZED);
        }
        /* --- Si no está autenticado, no tiene autorización para entrar a game_view --- */
        if (!authentication.getName().equals(gamePlayer.get().getPlayer().getEmail())) {
            return new ResponseEntity<>(Util.makeMap("problem", "Player not authorized."), HttpStatus.FORBIDDEN);
        }

        Map<String, Object> dto = new LinkedHashMap<>();
        Map<String, Object> hits = new LinkedHashMap<>();

        hits.put("self", hitsAndSink(gamePlayer.get(), gamePlayer.get().getOpponent()));
        hits.put("opponent", hitsAndSink(gamePlayer.get().getOpponent(), gamePlayer.get()));

        dto.put("id", gamePlayer.get().getGame().getId());
        dto.put("created", gamePlayer.get().getGame().getCreatedDate());
        dto.put("gameState", getGameState(gamePlayer.get()));

        dto.put("gamePlayers", gamePlayer.get().getGame().getGamePlayers()
                .stream()
                .map(gamePlayer1 -> gamePlayer1.gamePlayerDTO())
                .collect(Collectors.toList()));
        dto.put("ships", gamePlayer.get().getShips()
                .stream()
                .map(ship -> ship.shipDTO())
                .collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.get().getGame().getGamePlayers()
                .stream()
                .flatMap(gamePlayer1 -> gamePlayer1.getSalvos()
                        .stream()
                        .map(salvo -> salvo.salvoDTO()))
                .collect(Collectors.toList()));
        dto.put("hits", hits);

        return new ResponseEntity<>(dto, HttpStatus.OK);

    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> saveSalvos(@PathVariable long gamePlayerId, Authentication authentication,
                                                          @RequestBody Salvo salvo) {
        Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
        GamePlayer opponent = gamePlayer.get().getOpponent();
        if (Util.isGuest(authentication)) {
            return new ResponseEntity<>(Util.makeMap("error", "The player isn't authorized."), HttpStatus.UNAUTHORIZED);
        }

        if (!gamePlayer.isPresent()) {
            return new ResponseEntity<>(Util.makeMap("error", "That gamePlayer doesn't exists."), HttpStatus.UNAUTHORIZED);
        }

        if (!authentication.getName().equals(gamePlayer.get().getPlayer().getEmail())) {
            return new ResponseEntity<>(Util.makeMap("error", "You're not the current player."), HttpStatus.UNAUTHORIZED);
        }

        if (salvo.getSalvoLocations().size() > 5) {
            return new ResponseEntity<>(Util.makeMap("error", "You can't place more than 5 salvos in one turn."), HttpStatus.FORBIDDEN);
        }

        if (opponent.getId() != 0) {
            if (gamePlayer.get().getSalvos().size() <= opponent.getSalvos().size()) {
                salvo.setTurn(gamePlayer.get().getSalvos().size() + 1);
                salvo.setGamePlayer(gamePlayer.get());
                salvoRepository.save(salvo);

                return new ResponseEntity<>(Util.makeMap("OK", "Salvos created!"), HttpStatus.CREATED);

            } else {
                return new ResponseEntity<>(Util.makeMap("error", "Salvos are already placed in this turn."), HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "You don't have any opponent yet."), HttpStatus.FORBIDDEN);
        }

    }

    private List<Map> hitsAndSink(GamePlayer self, GamePlayer opponent) {

        List<Map> hits = new ArrayList<>();

        // Total damage

        int carrierDamage = 0;
        int battleshipDamage = 0;
        int submarineDamage = 0;
        int destroyerDamage = 0;
        int patrolboatDamage = 0;

        // Ship loc

        List<String> carrierLocations = findShipLocationsByType(opponent, "carrier");
        List<String> battleshipLocations = findShipLocationsByType(opponent, "battleship");
        List<String> submarineLocations = findShipLocationsByType(opponent, "submarine");
        List<String> destroyerLocations = findShipLocationsByType(opponent, "destroyer");
        List<String> patrolboatLocations = findShipLocationsByType(opponent, "patrolboat");

       /* List<Salvo> opponentSalvos = new ArrayList<>(opponent.getSalvos());
        opponentSalvos.sort(Comparator.comparing(Salvo::getId)); */// Meto todos los salvos en una lista y
        // los acomodo para que se muestren en
        // orden en el JSON

        for (Salvo salvo : self.getSalvos()) {

            List<String> hitCellsList = new ArrayList<>();

            int carrierHitsInTurn = 0;
            int battleshipHitsInTurn = 0;
            int submarineHitsInTurn = 0;
            int destroyerHitsInTurn = 0;
            int patrolboatHitsInTurn = 0;

            int missedShots = salvo.getSalvoLocations().size();

            for (String salvoShot : salvo.getSalvoLocations()) {

                if (carrierLocations.contains(salvoShot)) {
                    carrierDamage++;
                    carrierHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }

                if (battleshipLocations.contains(salvoShot)) {
                    battleshipDamage++;
                    battleshipHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }

                if (submarineLocations.contains(salvoShot)) {
                    submarineDamage++;
                    submarineHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }

                if (destroyerLocations.contains(salvoShot)) {
                    destroyerDamage++;
                    destroyerHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }

                if (patrolboatLocations.contains(salvoShot)) {
                    patrolboatDamage++;
                    patrolboatHitsInTurn++;
                    hitCellsList.add(salvoShot);
                    missedShots--;
                }
            }

            //DAÑOS POR TURNO

            Map<String, Object> damageDTO = new LinkedHashMap<String, Object>();
            damageDTO.put("carrierHits", carrierHitsInTurn);
            damageDTO.put("battleshipHits", battleshipHitsInTurn);
            damageDTO.put("submarineHits", submarineHitsInTurn);
            damageDTO.put("destroyerHits", destroyerHitsInTurn);
            damageDTO.put("patrolboatHits", patrolboatHitsInTurn);

            // DAÑOS TOTALES

            damageDTO.put("carrier", carrierDamage);
            damageDTO.put("battleship", battleshipDamage);
            damageDTO.put("submarine", submarineDamage);
            damageDTO.put("destroyer", destroyerDamage);
            damageDTO.put("patrolboat", patrolboatDamage);

            Map<String, Object> hitsMapLocations = new LinkedHashMap<>();

            hitsMapLocations.put("turn", salvo.getTurn());
            hitsMapLocations.put("hitLocations", hitCellsList);
            hitsMapLocations.put("damages", damageDTO);
            hitsMapLocations.put("missed", missedShots);

            hits.add(hitsMapLocations);

        }
        return hits;
    }

    private String getGameState(GamePlayer self) {

        if (self.getShips().size() == 0) {
            return "PLACESHIPS";
        }

        if (self.getGame().getGamePlayers().size() == 1) {
            return "WAITINGFOROPP";
        }

        if (self.getOpponent().getShips().isEmpty()) {
            return "WAIT";
        }

        if (self.getSalvos().size() < self.getOpponent().getSalvos().size()) {
            return "PLAY";
        }

        if (self.getSalvos().size() > self.getOpponent().getSalvos().size()) {
            return "WAIT";
        }

        if (self.getSalvos().size() == self.getOpponent().getSalvos().size()) {

            boolean selfLost = getIfAllSunk(self, self.getOpponent());
            boolean opponentLost = getIfAllSunk(self.getOpponent(), self);

            if (selfLost && opponentLost) {
                scoreRepository.save(new Score(self.getGame(), self.getPlayer(), 0.5, LocalDateTime.now()));

                return "TIE";
            }

            if (selfLost) {
                scoreRepository.save(new Score(self.getGame(), self.getPlayer(), 0.0, LocalDateTime.now()));

                return "LOST";
            }

            if (opponentLost) {
                scoreRepository.save(new Score(self.getGame(), self.getPlayer(), 1.0, LocalDateTime.now()));

                return "WON";
            }
        }

        return "PLAY";
    }

    private List<String> findShipLocationsByType(GamePlayer self, String type) {
        return self.getShips().size() == 0 ? new ArrayList<>() : self.getShips().stream().filter(ship -> ship.getType().name().replace("_", "").toLowerCase(Locale.ROOT).equals(type)).findFirst().orElse(new Ship()).getLocations();
    }

    private boolean getIfAllSunk(GamePlayer self, GamePlayer opponent) {
        if (!opponent.getShip().isEmpty() && !self.getSalvos().isEmpty()) {
            return opponent.getSalvos().stream().flatMap(salvo -> salvo.getSalvoLocations().stream()).collect(Collectors.toList())
                    .containsAll(self.getShip().stream().flatMap(ship -> ship.getLocations().stream()).collect(Collectors.toList()));
        }
        return false;
    }
}

