package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api") // Ruta
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/players")
    public List<Object> getPlayers() {
        return playerRepository.findAll()
                .stream().map(Player::playerDTO)
                .collect(Collectors.toList());
    }


    @PostMapping("/players")
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String email, @RequestParam String password) {
        /* --- Si email o password están vacías, tira error --- */
        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>(Util.makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByEmail(email) != null) { // si el mail del player ya existe
            return new ResponseEntity<>(Util.makeMap("error", "Name already exists!"), HttpStatus.FORBIDDEN);
        }
        playerRepository.save(new Player(email, passwordEncoder.encode(password)));

        return new ResponseEntity<>(Util.makeMap("message", "Success! Player created"), HttpStatus.CREATED);
    }
}
