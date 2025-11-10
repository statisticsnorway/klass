package no.ssb.klass.api.services;

import org.springframework.data.domain.Pageable;

public interface SearchService {

  org.springframework.data.domain.Page<OpenSearchResult> publicSearch(
      String query, Pageable pageable, String filterOnSection, boolean includeCodeLists);
}
