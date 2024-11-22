package org.dogoodthings.ectr.genericObjects.jira;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dogoodthings.ectr.genericObjects.jira.model.JiraIssue;
import org.dogoodthings.ectr.genericObjects.jira.model.JiraLink;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.dscsag.plm.spi.interfaces.ECTRService;
import com.dscsag.plm.spi.interfaces.objects.PlmObjectKey;
import com.dscsag.plm.spi.interfaces.services.ServiceResult;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectConfiguration;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectResult;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectService;
import com.google.gson.Gson;

@Component (property = {"implementation-alias=dogoodthings.JiraByNumber2"})
public class JiraLinkedIssuesTransformObjectService
    implements TransformObjectService
{
  @Reference private JiraConfiguration jiraConfiguration;
  @Reference private ECTRService ectrService;

  @Override
  public ServiceResult<TransformObjectResult> execute(TransformObjectConfiguration transformObjectConfiguration)
  {
    List<PlmObjectKey> plmObjectKeys = new ArrayList<>();
    for (PlmObjectKey plmObjectKey : transformObjectConfiguration.objectKeys())
      plmObjectKeys.addAll(findLinkedIssues(plmObjectKey));
    return ServiceResult.successWithObject(() -> plmObjectKeys);
  }

  private Collection<PlmObjectKey> findLinkedIssues(PlmObjectKey plmObjectKey)
  {
    List<PlmObjectKey> keys = new ArrayList<>();
    JiraIssue jiraIssue = getJiraIssue(plmObjectKey.getKey());
    if (jiraIssue != null)
    {
      for (JiraLink jiraLink : jiraIssue.fields().issuelinks())
      {
        if (jiraLink.outwardIssue() != null)
          keys.add(new PlmObjectKey("JIRA", jiraLink.outwardIssue().key()));
        if (jiraLink.inwardIssue() != null)
          keys.add(new PlmObjectKey("JIRA", jiraLink.inwardIssue().key()));
      }
    }
    return keys;
  }

  private JiraIssue getJiraIssue(String taskKey)
  {
    try
    {
      String ulr = jiraConfiguration.getJiraBaseUrl() + "/rest/api/2/issue/" + taskKey;
      HttpRequest request = HttpRequest.newBuilder().uri(new URI(ulr))
          .headers("Authorization", "Bearer " + jiraConfiguration.getJiraToken()).GET().build();
      final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
      String string = response.body();
      return new Gson().fromJson(string, JiraIssue.class);
    }
    catch (URISyntaxException | IOException | InterruptedException e)
    {
      ectrService.getPlmLogger().error("Exception trying to get jira issue ["+taskKey+ "]");
      ectrService.getPlmLogger().error(e);
    }
    return null;
  }

}
