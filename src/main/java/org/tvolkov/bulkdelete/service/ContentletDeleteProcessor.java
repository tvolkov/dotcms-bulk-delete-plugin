package org.tvolkov.bulkdelete.service;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.UserAPI;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.liferay.portal.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class ContentletDeleteProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentletDeleteProcessor.class);

    private static final String QUERY_TEMPLATE = "+identifier:";

    private ContentletAPI contentApi  = APILocator.getContentletAPI();
    private UserAPI userApi       = APILocator.getUserAPI();

    void deleteContentlets(List<String> identifiers){
        User user = getSystemUser();
        List<Contentlet> contentlets = new ArrayList<>();
        for (String identifier : identifiers){
            contentlets.add(getContentletByIdentifier(identifier, user));
        }

        if (processBulkDelete(contentlets, user)){
            LOGGER.info("contentlets were successfully deleted");
        } else {
            LOGGER.info("unable to remove some of the contentlets");
        }
    }

    private Contentlet getContentletByIdentifier(String identifier, User user){
        LOGGER.info("looking up contentlet with identifier " + identifier);
        String query = QUERY_TEMPLATE + identifier;
        Contentlet contentlet;
        try {
            List<Contentlet> contentlets = contentApi.search(query, 1, 0, "modDate", user, false);
            if (contentlets.size() > 0){
                LOGGER.info("Contentlets size = " + contentlets.size());
                contentlet = contentlets.get(0);
            } else {
                LOGGER.info("Contentlet with identifier " + identifier + " not found");
                return null;
            }
            LOGGER.info("Contentlet: " + contentlet.toString());
        } catch (DotDataException | DotSecurityException e) {
            throw new RuntimeException("Exception while getting contentlet with identifier " + identifier) ;
        }
        return contentlet;
    }

    private User getSystemUser(){
        User user;
        try {
            user = userApi.getSystemUser();
        } catch (DotDataException e) {
            throw new RuntimeException("Unable to get system user");
        }
        return user;
    }

    private boolean processBulkDelete(List<Contentlet> contentlets, User user){
        boolean result = false;
        try {
            HibernateUtil.startTransaction();
            contentApi.unpublish(contentlets, user, false);
            contentApi.archive(contentlets, user, false);
            result = result || contentApi.delete(contentlets, user, false);
            HibernateUtil.commitTransaction();
        } catch (DotDataException | DotSecurityException e) {
            LOGGER.error("exception while trying to delete contentlets: " + e.getMessage());
            try {
                HibernateUtil.rollbackTransaction();
            } catch (DotHibernateException e1) {
                LOGGER.error("unable to rollback transaction");
            }
        }
        return result;
    }
}
