package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.Changelog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Created by jro on 18.04.2017. */
@Repository
public interface ChangelogRepository extends JpaRepository<Changelog, Long> {}
