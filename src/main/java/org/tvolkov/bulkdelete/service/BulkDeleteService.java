package org.tvolkov.bulkdelete.service;

import com.dotcms.repackage.org.json.JSONArray;
import com.dotcms.repackage.org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tvolkov.bulkdelete.exceptions.BodyParsingException;

import java.util.ArrayList;
import java.util.List;

@Service
public class BulkDeleteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkDeleteService.class);

    private ContentletDeleteProcessor contentletDeleteProcessor;

    public BulkDeleteService(ContentletDeleteProcessor contentletDeleteProcessor){
        this.contentletDeleteProcessor = contentletDeleteProcessor;
    }

    public boolean deleteContentlets(String body){
        if (body == null){
            throw new BodyParsingException();
        }
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(body);
            LOGGER.info(jsonArray.toString());
        } catch (JSONException e) {
            LOGGER.error("got exception: " + e.getMessage());
            throw new BodyParsingException(e);
        }

        if (jsonArray.length() == 0) {
            LOGGER.error("empty json array");
            throw new BodyParsingException();
        }
        List<String> identifiers = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            String identifier;
            try {
                identifier = jsonArray.getString(i);
            } catch (JSONException e) {
                LOGGER.error("got exception: " + e.getMessage());
                throw new BodyParsingException(e);
            }
            identifiers.add(identifier);
        }
        return contentletDeleteProcessor.deleteContentlets(identifiers);
    }
}