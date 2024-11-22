package org.dogoodthings.ectr.genericObjects.jira.model;

import java.util.List;

public record JiraField(String description, String summary, List<JiraLink> issuelinks, JiraAssignee assignee) {
  private static final JiraAssignee dummyAssignee = new JiraAssignee("--", "", "", "--");

  public JiraAssignee assignee() {
    if (assignee == null)
      return dummyAssignee;
    else
      return assignee;
  }
}
