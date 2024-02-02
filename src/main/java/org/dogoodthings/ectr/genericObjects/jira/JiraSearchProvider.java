package org.dogoodthings.ectr.genericObjects.jira;

import com.dscsag.plm.spi.interfaces.search.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vko
 */

public class JiraSearchProvider implements SearchProvider {
  private final ServiceTool serviceTool;
  private final JiraConfiguration jiraConfiguration;

  public JiraSearchProvider(ServiceTool serviceTool) {
    this.serviceTool = serviceTool;
    jiraConfiguration = new JiraConfiguration(serviceTool);
  }


  @Override
  public SearchTask createTask(SearchQuery searchQuery) {
    return new SearchTask() {
      @Override
      public SearchResult search() {
        final List<SearchTerm> terms = searchQuery.getTerms();
        final String text = terms.stream().map(SearchTerm::getText).collect(Collectors.joining("%20"));
        var plmObjectKeys = new JiraSearchTool(serviceTool).search(text);
        List<SearchHit> hits = plmObjectKeys.stream().map(SearchHitFactory::createSearchHit).toList();
        return () -> hits;
      }

      @Override
      public void cancel() {
        //
      }
    };
  }
}
