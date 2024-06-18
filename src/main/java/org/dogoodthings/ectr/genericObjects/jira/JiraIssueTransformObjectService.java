package org.dogoodthings.ectr.genericObjects.jira;

import com.dscsag.plm.spi.interfaces.objects.PlmObjectKey;
import com.dscsag.plm.spi.interfaces.services.ServiceResult;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectConfiguration;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectResult;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectService;

import java.util.stream.Collectors;

public class JiraIssueTransformObjectService implements TransformObjectService {

  private final ServiceTool serviceTool;

  public JiraIssueTransformObjectService(ServiceTool serviceTool) {
    this.serviceTool = serviceTool;
  }

  @Override
  public ServiceResult<TransformObjectResult> execute(TransformObjectConfiguration transformObjectConfiguration) {
    String textToSearch = transformObjectConfiguration.objectKeys().stream().map(PlmObjectKey::getKey).collect(Collectors.joining("%20"));
    var plmObjectKeys = new JiraSearchTool(serviceTool).search(textToSearch);
    return ServiceResult.successWithObject(() -> plmObjectKeys);
  }

}
