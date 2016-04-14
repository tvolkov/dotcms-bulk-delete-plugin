package org.tvolkov.bulkdelete.osgi;

import com.dotcms.repackage.org.apache.felix.http.api.ExtHttpService;
import com.dotcms.repackage.org.osgi.framework.BundleContext;
import com.dotcms.repackage.org.osgi.framework.ServiceReference;
import com.dotmarketing.filters.CMSFilter;
import com.dotmarketing.osgi.GenericBundleActivator;
import org.springframework.web.servlet.DispatcherServlet;

public class Activator extends GenericBundleActivator {

    @SuppressWarnings ("unchecked")
    @Override
    public void start (BundleContext context) throws Exception {
        initializeServices(context);

        //Service reference to ExtHttpService that will allows to register servlets and filters
        ServiceReference sRef = context.getServiceReference(ExtHttpService.class.getName());
        if (sRef != null) {
            publishBundleServices(context);

            ExtHttpService httpService = (ExtHttpService) context.getService(sRef);
            try {
                DispatcherServlet dispatcherServlet = new DispatcherServlet();
                dispatcherServlet.setContextConfigLocation("spring/beans.xml");
                httpService.registerServlet("/spring", dispatcherServlet, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            CMSFilter.addExclude("/app/spring");
        }
    }

    @Override
    public void stop (BundleContext context) throws Exception {
        CMSFilter.removeExclude("/app/spring");
        unregisterServices(context);
    }

}