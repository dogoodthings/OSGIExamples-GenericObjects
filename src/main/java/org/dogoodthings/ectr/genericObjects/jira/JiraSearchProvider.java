package org.dogoodthings.ectr.genericObjects.jira;

import com.dscsag.plm.spi.interfaces.ECTRService;
import com.dscsag.plm.spi.interfaces.search.SearchHit;
import com.dscsag.plm.spi.interfaces.search.SearchHitFactory;
import com.dscsag.plm.spi.interfaces.search.SearchProvider;
import com.dscsag.plm.spi.interfaces.search.SearchQuery;
import com.dscsag.plm.spi.interfaces.search.SearchResult;
import com.dscsag.plm.spi.interfaces.search.SearchTask;
import com.dscsag.plm.spi.interfaces.search.SearchTerm;
import java.util.List;
import java.util.stream.Collectors;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author vko
 */
@Component
public class JiraSearchProvider implements SearchProvider {

  @Reference private JiraSearchTool jiraSearchTool;
  @Reference private ECTRService ectrService;

  @Override
  public SearchTask createTask(SearchQuery searchQuery) {
    return new JiraSearchTask(jiraSearchTool, ectrService, searchQuery);
  }

  private record JiraSearchTask(
      JiraSearchTool jiraSearchTool, ECTRService ectrService, SearchQuery query)
      implements SearchTask {

    @Override
    public SearchResult search() {
      final List<SearchTerm> terms = query.getTerms();
      final String text =
          terms.stream().map(SearchTerm::getText).collect(Collectors.joining("%20"));
      var plmObjectKeys = jiraSearchTool.search(text);
      List<SearchHit> hits = plmObjectKeys.stream().map(SearchHitFactory::createSearchHit).toList();
      return () -> hits;
    }

    @Override
    public void cancel() {
      ectrService.getPlmLogger().debug("JiraSearchTask: cancel is not supported.");
    }
  }
}
