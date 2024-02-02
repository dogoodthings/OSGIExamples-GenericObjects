package org.dogoodthings.ectr.genericObjects.jira;

import com.dscsag.plm.spi.interfaces.search.SearchProvider;
import com.dscsag.plm.spi.interfaces.services.ServiceConstants;
import com.dscsag.plm.spi.interfaces.services.genericobjects.PluginGenericTypeConfigurationFactory;
import com.dscsag.plm.spi.interfaces.services.transform.TransformObjectService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Dictionary;
import java.util.Hashtable;


public class Activator implements BundleActivator {
  public void start(BundleContext context) throws Exception {
    ServiceTool st = new ServiceTool(context);
    context.registerService(PluginGenericTypeConfigurationFactory.class, new JiraGenericObjectsFactory(st), null);
    context.registerService(SearchProvider.class, new JiraSearchProvider(st), null);
    Dictionary<String, Object> props = new Hashtable<>();
    props.put(ServiceConstants.IMPLEMENTATION_ALIAS, "dogoodthings.JiraByNumber");
    context.registerService(TransformObjectService.class, new JiraIssueTransformObjectService(st), props);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    // empty
  }
}
