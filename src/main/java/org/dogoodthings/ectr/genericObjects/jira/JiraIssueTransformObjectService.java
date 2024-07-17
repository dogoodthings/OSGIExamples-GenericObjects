package org.dogoodthings.ectr.genericObjects.jira;

import java.util.stream.Collectors;

import com.dscsag.plm.spi.interfaces.objects.PlmObjectKey;
import com.dscsag.plm.spi.interfaces.services.ServiceResult;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectConfiguration;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectResult;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(property = {"implementation-alias=dogoodthings.JiraByNumber"})
public class JiraIssueTransformObjectService implements TransformObjectService {

  @Reference private JiraSearchTool jiraSearchTool;

  @Override
  public ServiceResult<TransformObjectResult> execute(
      TransformObjectConfiguration transformObjectConfiguration) {
    String textToSearch =
        transformObjectConfiguration.objectKeys().stream()
            .map(PlmObjectKey::getKey)
            .collect(Collectors.joining("%20"));
    var plmObjectKeys = jiraSearchTool.search(textToSearch);
    return ServiceResult.successWithObject(() -> plmObjectKeys);
  }
}
