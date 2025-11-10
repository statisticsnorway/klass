package no.ssb.klass.core.service;

import no.ssb.klass.core.model.Changelog;

/**
 * @author Mads Lundemo, SSB.
 */
public interface ChangeLogService {
  void deleteChangelog(Changelog changelog);
}
