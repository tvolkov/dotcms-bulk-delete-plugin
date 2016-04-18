package org.tvolkov.bulkdelete.service;

import com.dotcms.repackage.edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.tvolkov.bulkdelete.exceptions.BodyParsingException;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BulkDeleteServiceTest {
    @Mock
    private ContentletDeleteProcessor contentletDeleteProcessor;
    private BulkDeleteService bulkDeleteService;

    @Before
    public void setUp(){
        this.bulkDeleteService = new BulkDeleteService(contentletDeleteProcessor);
    }

    @Test(expected = BodyParsingException.class)
    public void shouldThrowAnExceptionIfInputStringIsNull(){
        //given
        String inputString = null;
        //when
        bulkDeleteService.deleteContentlets(inputString);
    }

    @Test(expected = BodyParsingException.class)
    public void shouldThrowAnExceptionIfInputStringIsEmpty(){
        //given
        String inputString = "";

        //when
        bulkDeleteService.deleteContentlets(inputString);
    }

    @Test(expected = BodyParsingException.class)
    public void shouldThrowAnExceptionIfInputStringIsNotAJsonArray(){
        //given
        String inputString = "1234";

        //when
        bulkDeleteService.deleteContentlets(inputString);
    }

    @Test(expected = BodyParsingException.class)
    public void shouldThrowAnExceptionIfInputStringIsAJsonArrayOfZeroLength(){
        //given
        String inputString = "[]";

        //when
        bulkDeleteService.deleteContentlets(inputString);
    }

    @Test
    public void shouldDeleteContentlet(){
        //given
        String inputString = "[123, 456]";

        //when
        bulkDeleteService.deleteContentlets(inputString);

        //then
        String[] ids = {"123", "456"};
        verify(contentletDeleteProcessor).deleteContentlets(Arrays.asList(ids));
    }

}