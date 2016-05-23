package za.co.entelect.web.config.filters;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import za.co.entelect.UnitTest;
import za.co.entelect.config.ConfigProperties;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class ErrorHandlerFilterTest {

    private ConfigProperties config = mock(ConfigProperties.class);
    private ErrorHandlerFilter classUnderTest = new ErrorHandlerFilter();

    @Before
    public void setUp() {
        reset(config);

        classUnderTest.setConfig(config);
    }

    @Test(expected = ServletException.class)
    public void testFailedRequestThrowsExceptionInDev() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(config.isDevOn()).thenReturn(true);
        doThrow(new ServletException()).when(filterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));

        classUnderTest.doFilter(request, response, filterChain);
    }

    @Test
    public void testFailedRequestShowsFriendlyErrorPageInProd() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        RequestDispatcher requestDispatcher = mock(RequestDispatcher.class);

        when(config.isDevOn()).thenReturn(false);
        doThrow(new ServletException()).when(filterChain).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        when(request.getRequestDispatcher(eq("/viewError"))).thenReturn(requestDispatcher);

        classUnderTest.doFilter(request, response, filterChain);

        verify(requestDispatcher, times(1)).forward(eq(request), eq(response));
    }
}
