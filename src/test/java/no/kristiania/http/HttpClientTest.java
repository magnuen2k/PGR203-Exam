package no.kristiania.http;

import no.kristiania.http.HttpClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpClientTest {

    @Test
    void showStatusCodeOK() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void hostNotFound() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void returnResponseHeaderContentLength() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?body=rplife");
        assertEquals("6", client.getResponseHeader("Content-Length"));
    }

    @Test
    void returnBodyContent() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?body=Jeg+har+vondt+i+Maven");
        assertEquals("Jeg har vondt i Maven", client.getResponseBody());
    }
}