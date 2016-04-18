package org.tvolkov.bulkdelete.service;

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
import org.tvolkov.bulkdelete.exceptions.ContentletNotFoundException;
import org.tvolkov.bulkdelete.exceptions.InternalServerError;
import org.tvolkov.bulkdelete.exceptions.NoIdentifiersException;

import java.util.ArrayList;
import java.util.List;

class ContentletDeleteProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentletDeleteProcessor.class);

    private static final String QUERY_TEMPLATE = "+identifier:";

    private ContentletAPI contentletApi;
    private UserAPI userApi;

    public ContentletDeleteProcessor(ContentletAPI contentletApi, UserAPI userApi){
        this.contentletApi = contentletApi;
        this.userApi = userApi;
    }

    boolean deleteContentlets(List<String> identifiers){
        if (identifiers == null || identifiers.isEmpty()){
            throw new NoIdentifiersException();
        }
        User user = getSystemUser();
        List<Contentlet> contentlets = new ArrayList<>();
        for (String identifier : identifiers){
            contentlets.add(getContentletByIdentifier(identifier, user));
        }

        return processBulkDelete(contentlets, user);
    }

    private Contentlet getContentletByIdentifier(String identifier, User user){
        LOGGER.debug("looking up contentlet with identifier " + identifier);
        String query = QUERY_TEMPLATE + identifier;
        Contentlet contentlet;
        try {
            List<Contentlet> contentlets = contentletApi.search(query, 1, 0, "modDate", user, false);
            if (contentlets.size() > 0){
                contentlet = contentlets.get(0);
            } else {
                LOGGER.info("Contentlet with identifier " + identifier + " not found");
                throw new ContentletNotFoundException();
            }
            LOGGER.debug("Contentlet: " + contentlet.toString());
        } catch (DotDataException | DotSecurityException e) {
            LOGGER.info("Exception while getting contentlet with identifier " + identifier);
            throw new InternalServerError() ;
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
        boolean result;
        try {
            HibernateUtil.startTransaction();
            contentletApi.unpublish(contentlets, user, false);
            contentletApi.archive(contentlets, user, false);
            result = contentletApi.delete(contentlets, user, false);
            HibernateUtil.commitTransaction();
        } catch (DotDataException | DotSecurityException e) {
            LOGGER.error("exception while trying to delete contentlets: " + e.getMessage());
            try {
                HibernateUtil.rollbackTransaction();
            } catch (DotHibernateException e1) {
                LOGGER.error("unable to rollback transaction");
            }
            LOGGER.info("exception while trying to delete contentlets: " + e.getMessage());
            throw new InternalServerError();
        }
        return result;
    }
}
