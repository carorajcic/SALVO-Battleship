package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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

    /*@GetMapping("/games")
    private Map<String, Object> getGames() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("games", gameRepository.findAll()
                .stream().map(Game::gameDTO)
                .collect(Collectors.toList()));
        return dto;
    }*/

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication){
        Map<String,Object> dto = new LinkedHashMap<>();

        if(!isGuest(authentication)){ // si isGuest devuelve true, pasa a false por !
            dto.put("player", playerRepository.findByEmail(authentication.getName()).playerDTO());
        } else
            dto.put("player", "Guest"); //authentication método de Spring, tiene el dato de quien está autenticado
        dto.put("games", gameRepository.findAll().stream()
                .map(Game::gameDTO).collect(Collectors.toList()));

        return dto;
    }

    @GetMapping("/players")
    public List<Object> getPlayers() {
        return playerRepository.findAll()
                .stream().map(Player::playerDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/players")
    public ResponseEntity<Map<String,Object>> register (
            @RequestParam String email, @RequestParam String password){
        if (email.isEmpty() || password.isEmpty()){ // si email o password están vacíos, error
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }
        if(playerRepository.findByEmail(email) != null){
            return new ResponseEntity<>(makeMap("error", "Name already exists!"), HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));

        return new ResponseEntity<>(makeMap("message", "Success! Player created"), HttpStatus.CREATED);
    }

    private boolean isGuest(Authentication authentication){
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    } // true

    @GetMapping("/gamePlayers")
    public List<Object> getGamePlayers() {
        return gamePlayerRepository.findAll()
                .stream().map(GamePlayer::gamePlayerDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> gamePlayers(@PathVariable long gamePlayerId) {
        Optional<GamePlayer> gp = gamePlayerRepository.findById(gamePlayerId);

        ResponseEntity<Map<String, Object>> response;
        if (gp.isPresent()) {
            response = new ResponseEntity<>(gp.get().game_view(), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(makeMap("problem", "gamePlayer doesn't exist"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}

