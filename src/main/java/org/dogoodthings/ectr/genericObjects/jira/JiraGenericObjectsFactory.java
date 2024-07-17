package org.dogoodthings.ectr.genericObjects.jira;

import com.dscsag.plm.spi.interfaces.ECTRService;
import com.dscsag.plm.spi.interfaces.objects.PlmObjectKey;
import com.dscsag.plm.spi.interfaces.services.genericobjects.BasicDetailsProvider;
import com.dscsag.plm.spi.interfaces.services.genericobjects.GenericFieldSpecificationFactory;
import com.dscsag.plm.spi.interfaces.services.genericobjects.ObjectSpecificationProvider;
import com.dscsag.plm.spi.interfaces.services.genericobjects.PluginGenericTypeConfigurationFactory;
import com.dscsag.plm.spi.interfaces.services.genericobjects.PluginGenericTypeHandler;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.dogoodthings.ectr.genericObjects.jira.model.JiraIssue;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author vko
 */
@Component
public class JiraGenericObjectsFactory implements PluginGenericTypeConfigurationFactory {

  @Reference private ECTRService ectrService;

  @Reference private JiraConfiguration jiraConfiguration;

  @Override
  public Map<String, PluginGenericTypeHandler> getTypeHandlerMap() {
    log("getTypeHandlerMap()");
    return Map.of("JIRA", new JiraHandler());
  }

  private void log(String message) {
    ectrService.getPlmLogger().trace("jira: " + message);
  }

  private JiraIssue executeRequest(String taskKey) {
    try {
      String ulr = jiraConfiguration.getJiraBaseUrl() + "/rest/api/2/issue/" + taskKey;
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI(ulr))
              .headers("Authorization", "Bearer " + jiraConfiguration.getJiraToken())
              .GET()
              .build();
      final HttpResponse<String> response =
          HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      String string = response.body();
      log(string);
      return new Gson().fromJson(string, JiraIssue.class);
    } catch (URISyntaxException | IOException | InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  private class JiraHandler implements PluginGenericTypeHandler {

    @Override
    public ObjectSpecificationProvider getFieldSpecificationProvider() {
      log("getFieldSpecificationProvider()");
      return () ->
          List.of(
              GenericFieldSpecificationFactory.createTextField("KEY", "Key", 100),
              GenericFieldSpecificationFactory.createTextField("ID", "Id", 100),
              GenericFieldSpecificationFactory.createTextField("DESCRIPTION", "Description", 100),
              GenericFieldSpecificationFactory.createTextField("SUMMARY", "Summary", 100));
    }

    @Override
    public BasicDetailsProvider getBasicDetailsProvider() {
      log("getBasicDetailsProvider()");
      return new JiraDataProvider();
    }

    private class JiraDataProvider implements BasicDetailsProvider {

      private final Map<String, Function<JiraIssue, Object>> fieldMapper;

      JiraDataProvider() {
        fieldMapper = new HashMap<>();
        fieldMapper.put("KEY", JiraIssue::key);
        fieldMapper.put("ID", JiraIssue::id);
        fieldMapper.put("DESCRIPTION", issue -> issue.fields().description());
        fieldMapper.put("SUMMARY", issue -> issue.fields().summary());
      }

      @Override
      public Map<PlmObjectKey, Map<String, Object>> provideBasicDetails(
          Collection<PlmObjectKey> poks) {
        Map<PlmObjectKey, Map<String, Object>> details = new HashMap<>();
        for (PlmObjectKey pok : poks) {
          final String taskKey = pok.getKey();

          final JiraIssue issue = executeRequest(taskKey);
          final Map<String, Object> objectDetails =
              fieldMapper.entrySet().stream()
                  .map(e -> Map.entry(e.getKey(), e.getValue().apply(issue)))
                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
          details.put(pok, objectDetails);
        }
        return details;
      }
    }
  }
}
