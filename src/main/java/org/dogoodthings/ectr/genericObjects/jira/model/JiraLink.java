package org.dogoodthings.ectr.genericObjects.jira.model;

public record JiraLink(String id, JiraOutwardIssue outwardIssue, JiraInwardIssue inwardIssue) {
}
