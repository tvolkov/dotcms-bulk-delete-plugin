package org.tvolkov.bulkdelete.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "dotCMS was unable to process your request. Please try again")
public class InternalServerError extends RuntimeException {
    public InternalServerError(){
        super();
    }
}
