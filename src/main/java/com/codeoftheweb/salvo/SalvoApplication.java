package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository repository, GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository,
                                      SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
        return (args) -> {

            //Players
            Player player1 = repository.save(new Player("j.bauer@ctu.gov", passwordEncoder().encode("24")));
            Player player2 = repository.save(new Player("c.obrian@ctu.gov", passwordEncoder().encode("42")));
            Player player3 = repository.save(new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb")));
            Player player4 = repository.save(new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole")));

            //Games
            Game game1 = gameRepository.save(new Game(LocalDateTime.now()));
            Game game2 = gameRepository.save(new Game(LocalDateTime.now().plusHours(1)));
            Game game3 = gameRepository.save(new Game(LocalDateTime.now().plusHours(2)));
            Game game4 = gameRepository.save(new Game(LocalDateTime.now().plusHours(3)));

            //gamePlayers POR JUEGO
            GamePlayer gamePlayer1 = gamePlayerRepository.save(new GamePlayer(game1, player1));
            GamePlayer gamePlayer2 = gamePlayerRepository.save(new GamePlayer(game1, player2));

            GamePlayer gamePlayer3 = gamePlayerRepository.save(new GamePlayer(game2, player2));
            GamePlayer gamePlayer4 = gamePlayerRepository.save(new GamePlayer(game2, player1));

            GamePlayer gamePlayer5 = gamePlayerRepository.save(new GamePlayer(game3, player1));
            GamePlayer gamePlayer6 = gamePlayerRepository.save(new GamePlayer(game3, player4));

            GamePlayer gamePlayer7 = gamePlayerRepository.save(new GamePlayer(game4, player2));

            //Locations
            List<String> location1 = new ArrayList<>(Arrays.asList("H2", "H3", "H4"));
            List<String> location2 = new ArrayList<>(Arrays.asList("E1", "F1", "G1"));
            List<String> location3 = new ArrayList<>(Arrays.asList("B4", "B5"));
            List<String> location4 = new ArrayList<>(Arrays.asList("B5", "C5", "D5"));
            List<String> location5 = new ArrayList<>(Arrays.asList("F1", "F2"));
            List<String> location6 = new ArrayList<>(Arrays.asList("C6", "C7"));
            List<String> location7 = new ArrayList<>(Arrays.asList("A2", "A3", "A4"));
            List<String> location8 = new ArrayList<>(Arrays.asList("G6", "H6"));

            //Ships POR JUEGO
            Ship ship1 = new Ship(ShipType.DESTROYER, location1, gamePlayer1); //jbauer
            Ship ship2 = new Ship(ShipType.SUBMARINE, location2, gamePlayer1); // jbauer
            Ship ship3 = new Ship(ShipType.PATROL, location3, gamePlayer1); // jbauer
            Ship ship4 = new Ship(ShipType.DESTROYER, location4, gamePlayer2); // cobrian
            Ship ship5 = new Ship(ShipType.PATROL, location5, gamePlayer2); // cobrian

            Ship ship6 = new Ship(ShipType.DESTROYER, location4, gamePlayer3); // jbauer
            Ship ship7 = new Ship(ShipType.PATROL, location6, gamePlayer3); // jbauer
            Ship ship8 = new Ship(ShipType.SUBMARINE, location7, gamePlayer4); // cobrian
            Ship ship9 = new Ship(ShipType.PATROL, location8, gamePlayer4); // cobrian

            Ship ship10 = new Ship(ShipType.DESTROYER, location4, gamePlayer5); // cobrian
            Ship ship11 = new Ship(ShipType.PATROL, location6, gamePlayer5); // cobrian
            Ship ship12 = new Ship(ShipType.SUBMARINE, location7, gamePlayer6); // talmeida
            Ship ship13 = new Ship(ShipType.PATROL, location8, gamePlayer6); // talmeidia

            Ship ship14 = new Ship(ShipType.DESTROYER, location4, gamePlayer7); // cobrian
            Ship ship15 = new Ship(ShipType.PATROL, location6, gamePlayer7); // cobrian

            Salvo salvo1 = salvoRepository.save(new Salvo(gamePlayer1, 1, Arrays.asList("B5", "C5", "F1")));
            Salvo salvo2 = salvoRepository.save(new Salvo(gamePlayer1, 2, Arrays.asList("F2", "D5")));
            Salvo salvo3 = salvoRepository.save(new Salvo(gamePlayer2, 1, Arrays.asList("B3", "B4", "B5")));
            Salvo salvo4 = salvoRepository.save(new Salvo(gamePlayer2, 2, Arrays.asList("E1", "H3", "A2")));

            Salvo salvo5 = salvoRepository.save(new Salvo(gamePlayer3, 1, Arrays.asList("A2", "A4", "G6")));
            Salvo salvo6 = salvoRepository.save(new Salvo(gamePlayer3, 2, Arrays.asList("A3", "H6")));
            Salvo salvo7 = salvoRepository.save(new Salvo(gamePlayer4, 1, Arrays.asList("B5", "D5", "C7")));
            Salvo salvo8 = salvoRepository.save(new Salvo(gamePlayer4, 2, Arrays.asList("C5", "C6")));

            Salvo salvo9 = salvoRepository.save(new Salvo(gamePlayer5, 1, Arrays.asList("G6", "H6", "A4")));
            Salvo salvo10 = salvoRepository.save(new Salvo(gamePlayer5, 2, Arrays.asList("A2", "A3", "D8")));
            Salvo salvo11 = salvoRepository.save(new Salvo(gamePlayer6, 1, Arrays.asList("H1", "H2", "H3")));
            Salvo salvo12 = salvoRepository.save(new Salvo(gamePlayer6, 2, Arrays.asList("E1", "F2", "G3")));

            Salvo salvo13 = salvoRepository.save(new Salvo(gamePlayer7, 1, Arrays.asList("A3", "A4", "A7")));
            Salvo salvo14 = salvoRepository.save(new Salvo(gamePlayer7, 2, Arrays.asList("A2", "G6", "H6")));

            Score score1 = new Score(game1, player1, 0.5F, LocalDateTime.now());
            Score score2 = new Score(game1, player2, 0.5F, LocalDateTime.now());
            Score score3 = new Score(game2, player2, 0.5F, LocalDateTime.now());
            Score score4 = new Score(game2, player1, 0.5F, LocalDateTime.now());
            Score score5 = new Score(game3, player1, 1.0F, LocalDateTime.now());
            Score score6 = new Score(game3, player4, 0.0F, LocalDateTime.now());
            Score score7 = new Score(game4, player2, 1.0F, LocalDateTime.now());

            shipRepository.save(ship1);
            shipRepository.save(ship2);
            shipRepository.save(ship3);
            shipRepository.save(ship4);
            shipRepository.save(ship5);
            shipRepository.save(ship6);
            shipRepository.save(ship7);
            shipRepository.save(ship8);
            shipRepository.save(ship9);
            shipRepository.save(ship10);
            shipRepository.save(ship11);
            shipRepository.save(ship12);
            shipRepository.save(ship13);
            shipRepository.save(ship14);
            shipRepository.save(ship15);

            salvoRepository.save(salvo1);
            salvoRepository.save(salvo2);
            salvoRepository.save(salvo3);
            salvoRepository.save(salvo4);
            salvoRepository.save(salvo5);
            salvoRepository.save(salvo6);
            salvoRepository.save(salvo7);
            salvoRepository.save(salvo8);
            salvoRepository.save(salvo9);
            salvoRepository.save(salvo10);
            salvoRepository.save(salvo11);
            salvoRepository.save(salvo12);
            salvoRepository.save(salvo13);
            salvoRepository.save(salvo14);

            scoreRepository.save(score1);
            scoreRepository.save(score2);
            scoreRepository.save(score3);
            scoreRepository.save(score4);
            scoreRepository.save(score5);
            scoreRepository.save(score6);
            scoreRepository.save(score7);

        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}







