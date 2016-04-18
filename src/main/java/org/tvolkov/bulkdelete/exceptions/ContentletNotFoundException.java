package org.tvolkov.bulkdelete.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Contentlet not found")
public class ContentletNotFoundException extends RuntimeException {
    public ContentletNotFoundException(){
        super();
    }
}
