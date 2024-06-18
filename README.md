# OSGIExamples-GenericObjects

This example contains three aspects:

1. Enhancing ECTRs QuickSearch with own implementation (JiraSearchProvider implements SearchProvider)
2. Implementation of own generic objects using OSGI (JiraGenericObjectsFactory implements
   PluginGenericTypeConfigurationFactory)
3. Smart Container which using own implementation (JiraIssueTransformObjectService implements TransformObjectService)

copy directory "addons" found in "resources" into <ECTR_INSTDIR>
(or, which is basically the same, copy directory "genericObjects" found in "resources\addons" into <ECTR_INSTDIR>
\addons )
put the compiled jar into <ECTR_INSTDIR>\addons\genericObjects\basis\plugins
edit the file <ECTR_INSTDIR>\addons\genericObjects\customize\defaults.txt
and set the values for token and baseUrl.
The token is "personal access token" which you can create in jira.

(!) If you are using "backend config" then you have to put the file
<ECTR_INSTDIR>\addons\genericObjects\customize\defaults.txt into backend config directory

Start ECTR, press ctrl-f, search for jira issues using Quick Search.

Additionally, materials wiil habe a new Smart Container "related jira issues"
This container lists all jira issues where the text contains the material number


