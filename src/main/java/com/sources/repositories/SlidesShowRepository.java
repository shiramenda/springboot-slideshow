package com.sources.repositories;

import com.sources.entities.SlidesShow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlidesShowRepository extends JpaRepository<SlidesShow, Long> {
   List<SlidesShow> findByName(String name);

}
