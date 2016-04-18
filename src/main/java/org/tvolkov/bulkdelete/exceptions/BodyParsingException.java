package org.tvolkov.bulkdelete.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "invalid request body")
public class BodyParsingException extends RuntimeException {
    public BodyParsingException(){
        super();
    }

    public BodyParsingException(Exception e){
        super(e);
    }
}
