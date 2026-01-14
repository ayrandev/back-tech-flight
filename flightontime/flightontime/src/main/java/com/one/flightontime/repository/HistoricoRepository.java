package com.one.flightontime.repository;

import com.one.flightontime.domain.HistoricoPrevisao;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoRepository extends JpaRepository<@NonNull HistoricoPrevisao, @NonNull Long> {

}
