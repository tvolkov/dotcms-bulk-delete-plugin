package org.tvolkov.bulkdelete.service;

import com.dotcms.repackage.org.json.JSONArray;
import com.dotcms.repackage.org.json.JSONException;
import com.dotcms.repackage.org.json.JSONObject;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.UserAPI;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BulkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkService.class);
    private ContentletAPI contentApi  = APILocator.getContentletAPI();
    private UserAPI       userApi       = APILocator.getUserAPI();

    public void deleteContentlets(String body){
        LOGGER.info("parsing request body: " + body);
        JSONArray jsonArray;
        try {
            LOGGER.info("json array");
            jsonArray = new JSONArray(body);
            LOGGER.info(jsonArray.toString());
        } catch (JSONException e) {
            LOGGER.error("got exception: " + e.getMessage());
            throw new RuntimeException(e);
        }

        LOGGER.info("got json array: " + jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject;
            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            LOGGER.error("got json object: " + jsonObject);
            deleteContentlet(jsonObject.toString());
        }
    }

    private void deleteContentlet(String identifier){

    }
}