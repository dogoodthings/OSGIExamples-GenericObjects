package org.dogoodthings.ectr.genericObjects.jira;

import com.dscsag.plm.spi.interfaces.ECTRService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = JiraConfiguration.class)
public class JiraConfiguration {

  @Reference private ECTRService ectrService;

  public String getJiraToken() {
    String token = "";
    try {
      token =
          ectrService
              .getPlmPreferences()
              .stringValue("org.dogoodthings.ectr.genericObjects", "jira.token");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return token;
  }

  public String getJiraBaseUrl() {
    String baseUrl = "";
    try {
      baseUrl =
          ectrService
              .getPlmPreferences()
              .stringValue("org.dogoodthings.ectr.genericObjects", "jira.baseUrl");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return baseUrl;
  }
}
