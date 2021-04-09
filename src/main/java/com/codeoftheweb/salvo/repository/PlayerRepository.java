package com.codeoftheweb.salvo.repository;
//Un repositorio es una clase que administra el almacenamiento y la recuperación de instancias de clases en una base de datos

import com.codeoftheweb.salvo.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource// toma la instancia de clase del jugador y le crea el json, le define una url
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByEmail(@Param("email") String email);
}





