package no.kristiania.http;

import no.kristiania.http.QueryString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class QueryStringTest {

    @Test
    void shouldReturnQueryParameter() {
        QueryString queryString = new QueryString("status=200");
        assertEquals("200", queryString.getParameter("status"));
    }

    @Test
    void shouldReturnNullForMissingParameter() {
        QueryString queryString = new QueryString("status=404");
        assertNull(queryString.getParameter("body"));
    }

    @Test
    void shouldParseSeveralParameters() {
        QueryString queryString = new QueryString("status=200&body=Hello");
        assertEquals("200", queryString.getParameter("status"));
        assertEquals("Hello", queryString.getParameter("body"));
    }

    @Test
    void shouldSerializeQueryString() {
        QueryString queryString = new QueryString("status=200");
        assertEquals("?status=200", queryString.getQueryString());
        queryString.addParameter("body", "Hello");
        assertEquals("?status=200&body=Hello", queryString.getQueryString());
    }

}
