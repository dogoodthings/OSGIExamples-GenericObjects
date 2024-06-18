package org.dogoodthings.ectr.genericObjects.jira;

import com.dscsag.plm.spi.interfaces.objects.PlmObjectKey;
import com.google.gson.Gson;
import org.dogoodthings.ectr.genericObjects.jira.model.JiraSearchResult;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class JiraSearchTool {
  private final JiraConfiguration jiraConfiguration;

  public JiraSearchTool(ServiceTool serviceTool) {
    jiraConfiguration = new JiraConfiguration(serviceTool);
  }

  public List<PlmObjectKey> search(String text) {
    try {
      String queryUrl = jiraConfiguration.getJiraBaseUrl() + "/rest/api/2/search?jql=text~'" + text + "'";
      HttpRequest request = HttpRequest.newBuilder().uri(new URI(queryUrl)).headers("Authorization", "Bearer " + jiraConfiguration.getJiraToken()).GET().build();
      final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      final String body = response.body();
      JiraSearchResult jsr = new Gson().fromJson(body, JiraSearchResult.class);
      var plmObjectKeys = jsr.issues().stream().map(issue -> new PlmObjectKey("JIRA", issue.key())).toList();
      return plmObjectKeys;

    } catch (URISyntaxException | IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

}
