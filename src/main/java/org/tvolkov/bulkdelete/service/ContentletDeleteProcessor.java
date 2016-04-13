package org.tvolkov.bulkdelete.service;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.UserAPI;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.liferay.portal.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class ContentletDeleteProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentletDeleteProcessor.class);

    private static final String QUERY_TEMPLATE = "+identifier:";

    private ContentletAPI contentApi  = APILocator.getContentletAPI();
    private UserAPI userApi       = APILocator.getUserAPI();

    void deleteContentlet(String identifier){
        String query = QUERY_TEMPLATE + identifier;
        Contentlet contentlet;
        User user;
        LOGGER.info("looking up contentlet with identifier " + identifier);
        try {
            user = userApi.getSystemUser();
            List<Contentlet> contentlets = contentApi.search(query, 1, 0, "modDate", user, false);
            if (contentlets.size() > 0){
                LOGGER.info("Contentlets size = " + contentlets.size());
                contentlet = contentlets.get(0);
            } else {
                LOGGER.info("Contentlet with identifier " + identifier + " not found");
                return;
            }
            LOGGER.info("Contentlet: " + contentlet.toString());
        } catch (DotDataException e) {
            e.printStackTrace();
        } catch (DotSecurityException e) {
            e.printStackTrace();
        }
    }
}
