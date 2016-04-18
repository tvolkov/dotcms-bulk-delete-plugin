package org.tvolkov.bulkdelete.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Identifiers list is null or empty")
public class NoIdentifiersException extends RuntimeException {
    public NoIdentifiersException(){
        super();
    }
}
