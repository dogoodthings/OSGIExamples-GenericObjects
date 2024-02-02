package org.dogoodthings.ectr.genericObjects.jira;

import com.dscsag.plm.spi.interfaces.ECTRService;

public class JiraConfiguration {
  private final ServiceTool serviceTool;

  public JiraConfiguration(ServiceTool serviceTool) {
    this.serviceTool = serviceTool;
  }

  public String getJiraToken() {
    String token = "";
    try {
      token = serviceTool.getService(ECTRService.class).getPlmPreferences().stringValue("org.dogoodthings.ectr.genericObjects", "jira.token");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return token;
  }

  public String getJiraBaseUrl() {
    String baseUrl = "";
    try {
      baseUrl = serviceTool.getService(ECTRService.class).getPlmPreferences().stringValue("org.dogoodthings.ectr.genericObjects", "jira.baseUrl");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return baseUrl;
  }
}
