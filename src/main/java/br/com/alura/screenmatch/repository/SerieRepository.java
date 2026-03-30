package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    @Query("SELECT s FROM Serie s LEFT JOIN FETCH s.episodios WHERE LOWER(s.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))")
    Optional<Serie> findByTituloContainingIgnoreCaseWithEpisodios(@Param("titulo") String titulo);

    Optional<Serie> findByTituloContainingIgnoreCase(String titulo);
}
