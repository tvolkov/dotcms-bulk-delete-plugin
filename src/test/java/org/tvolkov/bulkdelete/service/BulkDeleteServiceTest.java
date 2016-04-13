package org.tvolkov.bulkdelete.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
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

    @Test(expected = RuntimeException.class)
    public void shouldThrowAnExceptionIfInputStringIsNull(){
        //given
        String inputString = null;
        //when
        bulkDeleteService.deleteContentlets(inputString);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowAnExceptionIfInputStringIsEmpty(){
        //given
        String inputString = "";

        //when
        bulkDeleteService.deleteContentlets(inputString);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowAnExceptionIfInputStringIsNotAJsonArray(){
        //given
        String inputString = "1234";

        //when
        bulkDeleteService.deleteContentlets(inputString);
    }

    @Test(expected = RuntimeException.class)
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
        verify(contentletDeleteProcessor).deleteContentlet("123");
        verify(contentletDeleteProcessor).deleteContentlet("456");
    }

}