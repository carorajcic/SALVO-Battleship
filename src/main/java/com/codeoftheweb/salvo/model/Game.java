package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    public LocalDateTime createdDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    public Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<Score> scores = new HashSet<>();

    public Game() {
    }

    public Game(LocalDateTime createdDate) {

        this.createdDate = createdDate;
    }

    public Long getId() {

        return id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public void addScore(Score score) {
        score.setGame(this);
        scores.add(score);
    }

    public Map<String, Object> gameDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("created", getCreatedDate());
        dto.put("gamePlayers", gamePlayers.stream()
                .map(GamePlayer::gamePlayerDTO).collect(Collectors.toSet()));
        dto.put("scores", this.scores.stream().map(score -> score.scoreDTO()).collect(Collectors.toSet()));
        return dto;
    }
}