package org.tvolkov.bulkdelete.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.tvolkov.bulkdelete.exceptions.InternalServerError;
import org.tvolkov.bulkdelete.service.BulkDeleteService;

@EnableWebMvc
@RequestMapping ("/bulk")
@Controller
public class BulkDeleteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkDeleteController.class);

    private BulkDeleteService bulkDeleteService;

    public BulkDeleteController(BulkDeleteService bulkDeleteService){
        this.bulkDeleteService = bulkDeleteService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody String get() {
        return "I'm alive";
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public @ResponseBody String processBulkDelete(@RequestBody String body){
        if (bulkDeleteService.deleteContentlets(body)){
            LOGGER.info("contentlets were successfully deleted");
            return "ok";
        } else {
            LOGGER.info("unable to remove some of the contentlets");
            throw new InternalServerError();
        }
    }

}