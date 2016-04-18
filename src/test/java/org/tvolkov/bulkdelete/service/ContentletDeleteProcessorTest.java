package org.tvolkov.bulkdelete.service;

import com.dotcms.repackage.edu.emory.mathcs.backport.java.util.Arrays;
import com.dotcms.repackage.edu.emory.mathcs.backport.java.util.Collections;
import com.dotmarketing.business.UserAPI;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.liferay.portal.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tvolkov.bulkdelete.exceptions.NoIdentifiersException;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
public class ContentletDeleteProcessorTest {

    @Mock
    private UserAPI userAPI;

    @Mock
    private ContentletAPI contentletAPI;

    private ContentletDeleteProcessor contentletDeleteProcessor;

    @Before
    public void setUp(){
        this.contentletDeleteProcessor = new ContentletDeleteProcessor(contentletAPI, userAPI);
    }

    @Test(expected = NoIdentifiersException.class)
    public void shouldThrowAnExceptionIfIdentifiersListIsNull(){
        //when
        this.contentletDeleteProcessor.deleteContentlets(null);
    }

    @Test(expected = NoIdentifiersException.class)
    public void shouldThrowAnExceptionIfIdentifiersListIsEmpty(){
        //when
        this.contentletDeleteProcessor.deleteContentlets(Collections.emptyList());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowAnExceptionIfContentletNotFound() throws DotSecurityException, DotDataException {
        //given
        String[] ids = {"123"};
        when(contentletAPI.search(eq("+identifier:123"), eq(1), eq(0), eq("modDate"), any(User.class), eq(false)))
                .thenReturn(Collections.emptyList());

        //when
        contentletDeleteProcessor.deleteContentlets(Arrays.asList(ids));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowARuntimeExceptionIfExceptionIsCaught() throws DotSecurityException, DotDataException {
        //given
        String[] ids = {"123"};
        when(contentletAPI.search(eq("+identifier:123"), eq(1), eq(0), eq("modDate"), any(User.class), eq(false)))
                .thenThrow(DotDataException.class);

        //when
        contentletDeleteProcessor.deleteContentlets(Arrays.asList(ids));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowAnExceptionIfExceptionWasThrownDuringBulkDelete() throws DotSecurityException,
            DotDataException {
        //given
        String[] ids = {"123", "456"};
        Contentlet contentlet = new Contentlet();
        doReturn(Collections.singletonList(contentlet))
                .when(contentletAPI)
                .search(eq("+identifier:123"), eq(1), eq(0), eq("modDate"), any(User.class), eq(false));
        doReturn(Collections.singletonList(contentlet))
                .when(contentletAPI)
                .search(eq("+identifier:456"), eq(1), eq(0), eq("modDate"), any(User.class), eq(false));
        when(contentletAPI.delete(any(List.class), any(User.class), eq(false))).thenThrow(DotSecurityException.class);

        //when
        contentletDeleteProcessor.deleteContentlets(Arrays.asList(ids));
    }

    @PrepareForTest({HibernateUtil.class})
    @Test
    public void shouldDeleteContentlets() throws DotSecurityException, DotDataException {
        //given
        String[] ids = {"123", "456"};
        Contentlet contentlet = new Contentlet();
        doReturn(Collections.singletonList(contentlet))
                .when(contentletAPI)
                .search(eq("+identifier:123"), eq(1), eq(0), eq("modDate"), any(User.class), eq(false));
        doReturn(Collections.singletonList(contentlet))
                .when(contentletAPI)
                .search(eq("+identifier:456"), eq(1), eq(0), eq("modDate"), any(User.class), eq(false));
        when(contentletAPI.delete(any(List.class), any(User.class), eq(false))).thenReturn(Boolean.TRUE);
        PowerMockito.mockStatic(HibernateUtil.class);

        //when
        contentletDeleteProcessor.deleteContentlets(Arrays.asList(ids));

        //then
        verify(contentletAPI).unpublish(any(List.class), any(User.class), eq(false));
        verify(contentletAPI).archive(any(List.class), any(User.class), eq(false));
        verify(contentletAPI).delete(any(List.class), any(User.class), eq(false));
    }
}