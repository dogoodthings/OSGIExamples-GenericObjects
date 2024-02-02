package org.dogoodthings.ectr.genericObjects.jira.model;

import java.util.List;

public record JiraSearchResult(List<JiraIssue> issues) {
}
