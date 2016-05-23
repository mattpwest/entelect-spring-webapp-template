package za.co.entelect.web.config.filters;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import za.co.entelect.UnitTest;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class RecaptchaResponseFilterTest {

    private RecaptchaResponseFilter classUnderTest = new RecaptchaResponseFilter();

    @Test
    public void testRequestWrappedWhenRecaptchaParamPresent() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getParameter(RecaptchaResponseFilter.RECAPTCHA_RESPONSE_ORIGINAL)).thenReturn("123");

        classUnderTest.doFilter(request, response, filterChain);

        ArgumentCaptor<ServletRequest> requestCaptor = ArgumentCaptor.forClass(ServletRequest.class);
        verify(filterChain, times(1))
            .doFilter(requestCaptor.capture(), any(HttpServletResponse.class));

        Assert.assertTrue(requestCaptor.getValue() instanceof RecaptchaResponseFilter.ModifiedHttpServerRequest);
    }

    @Test
    public void testRequestNotWrappedWhenRecaptchaParamNotPresent() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getParameter(eq(RecaptchaResponseFilter.RECAPTCHA_RESPONSE_ORIGINAL))).thenReturn(null);

        classUnderTest.doFilter(request, response, filterChain);

        ArgumentCaptor<ServletRequest> requestCaptor = ArgumentCaptor.forClass(ServletRequest.class);
        verify(filterChain, times(1))
            .doFilter(requestCaptor.capture(), any(HttpServletResponse.class));

        Assert.assertTrue(requestCaptor.getValue() instanceof HttpServletRequest);
    }
}
