package org.dogoodthings.ectr.genericObjects.jira;

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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dogoodthings.ectr.genericObjects.jira.model.JiraIssue;
import org.eclipse.jdt.annotation.NonNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.dscsag.plm.spi.interfaces.ECTRService;
import com.dscsag.plm.spi.interfaces.objects.PlmObjectKey;
import com.dscsag.plm.spi.interfaces.services.genericobjects.BasicDetailsProvider;
import com.dscsag.plm.spi.interfaces.services.genericobjects.GenericFieldSpecificationFactory;
import com.dscsag.plm.spi.interfaces.services.genericobjects.ObjectFieldSpecification;
import com.dscsag.plm.spi.interfaces.services.genericobjects.ObjectSpecificationProvider;
import com.dscsag.plm.spi.interfaces.services.genericobjects.PluginGenericTypeConfigurationFactory;
import com.dscsag.plm.spi.interfaces.services.genericobjects.PluginGenericTypeHandler;
import com.google.gson.Gson;

/**
 * @author vko
 */
@Component public class JiraGenericObjectsFactory implements PluginGenericTypeConfigurationFactory
{

  @Reference private ECTRService ectrService;

  @Reference private JiraConfiguration jiraConfiguration;

  @Override
  public Map<String, PluginGenericTypeHandler> getTypeHandlerMap()
  {
    log("getTypeHandlerMap()");
    return Map.of("JIRA", new JiraHandler());
  }

  private void log(String message)
  {
    ectrService.getPlmLogger().trace("jira: " + message);
  }

  protected JiraIssue executeRequest(String taskKey)
  {
    try
    {
      String ulr = jiraConfiguration.getJiraBaseUrl() + "/rest/api/2/issue/" + taskKey;
      HttpRequest request = HttpRequest.newBuilder().uri(new URI(ulr))
          .headers("Authorization", "Bearer " + jiraConfiguration.getJiraToken()).GET().build();
      final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      String string = response.body();
      log(string);
      return new Gson().fromJson(string, JiraIssue.class);
    }
    catch (URISyntaxException | IOException | InterruptedException e)
    {
      ectrService.getPlmLogger().error("Exception trying to get jira issue ["+taskKey+ "]");
      ectrService.getPlmLogger().error(e);
    }
    return null;
  }

  private class JiraHandler implements PluginGenericTypeHandler
  {

    @Override
    public ObjectSpecificationProvider getFieldSpecificationProvider()
    {
      log("getFieldSpecificationProvider()");
      return new JiraSpecificationProvider();
    }

    @Override
    public BasicDetailsProvider getBasicDetailsProvider()
    {
      log("getBasicDetailsProvider()");
      return new JiraDataProvider();
    }

    private static class JiraSpecificationProvider implements ObjectSpecificationProvider
    {
      @Override
      public List<ObjectFieldSpecification> getFieldSpecifications()
      {
        return List.of(GenericFieldSpecificationFactory.createTextField("KEY", "Key", 100),
            GenericFieldSpecificationFactory.createTextField("ID", "Id", 100),
            GenericFieldSpecificationFactory.createTextField("DESCRIPTION", "Description", 100),
            GenericFieldSpecificationFactory.createTextField("SUMMARY", "Summary", 100),
            GenericFieldSpecificationFactory.createTextField("ASSIGNEE", "Assignee", 100),
            GenericFieldSpecificationFactory.createTextField("ASSIGNEE-EMAIL", "EMail", 100));
      }

      //override omitted to be "silently" compatible with plm-api 5.2.10.0
      public @NonNull Set<String> getSupportedFeatures()
      {
        return Set.of("PREVIEW");
      }
    }

    private class JiraDataProvider implements BasicDetailsProvider
    {

      private final Map<String, Function<JiraIssue, Object>> fieldMapper;

      JiraDataProvider()
      {
        fieldMapper = new HashMap<>();
        fieldMapper.put("KEY", JiraIssue::key);
        fieldMapper.put("ID", JiraIssue::id);
        fieldMapper.put("DESCRIPTION", issue -> issue.fields().description());
        fieldMapper.put("SUMMARY", issue -> issue.fields().summary());
        fieldMapper.put("ASSIGNEE", issue -> issue.fields().assignee().displayName());
        fieldMapper.put("ASSIGNEE-EMAIL", issue -> issue.fields().assignee().emailAddress());
      }

      @Override
      public Map<PlmObjectKey, Map<String, Object>> provideBasicDetails(Collection<PlmObjectKey> poks)
      {
        Map<PlmObjectKey, Map<String, Object>> details = new HashMap<>();
        for (PlmObjectKey pok : poks)
        {
          final String taskKey = pok.getKey();

          final JiraIssue issue = executeRequest(taskKey);
          final Map<String, Object> objectDetails = fieldMapper.entrySet().stream()
              .map(e -> Map.entry(e.getKey(), e.getValue().apply(issue))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
          details.put(pok, objectDetails);
        }
        return details;
      }
    }
  }
}
