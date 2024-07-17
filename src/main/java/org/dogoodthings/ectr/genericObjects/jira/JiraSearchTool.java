package org.dogoodthings.ectr.genericObjects.jira;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import com.dscsag.plm.spi.interfaces.objects.PlmObjectKey;
import com.google.gson.Gson;

import org.dogoodthings.ectr.genericObjects.jira.model.JiraSearchResult;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = JiraSearchTool.class)
public class JiraSearchTool {

  @Reference private JiraConfiguration jiraConfiguration;

  public List<PlmObjectKey> search(String text) {
    try {
      String queryUrl =
          jiraConfiguration.getJiraBaseUrl() + "/rest/api/2/search?jql=text~'" + text + "'";
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI(queryUrl))
              .headers("Authorization", "Bearer " + jiraConfiguration.getJiraToken())
              .GET()
              .build();
      final HttpResponse<String> response =
          HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      final String body = response.body();
      JiraSearchResult jsr = new Gson().fromJson(body, JiraSearchResult.class);
      var plmObjectKeys =
          jsr.issues().stream().map(issue -> new PlmObjectKey("JIRA", issue.key())).toList();
      return plmObjectKeys;

    } catch (URISyntaxException | IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }
}
