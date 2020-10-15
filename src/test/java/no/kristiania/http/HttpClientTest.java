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

    
    /*
    public String returnStatus(int code) throws IOException {
        String res = null;
        no.kristiania.http.HttpClient client = new no.kristiania.http.HttpClient("urlecho.appspot.com", 80, "/echo");
        if (client.getStatusCode() > 500 || client.getStatusCode() < 0) {
            res = "Invalid status code";
        }
        return res;
    }

    @Test
    void affirmInvalidCode() throws IOException {
        no.kristiania.http.HttpClient client = new no.kristiania.http.HttpClient("urlecho.appspot.com", 80, "/echo?status=10000");
        assertEquals("Invalid status code", returnStatus(600));
        assertEquals("Invalid status code", returnStatus(-100));
    }
    */


    @Test
    void hostNotFound() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }



    @Test
    void returnResponseHeaderContentLength() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?body=Laurent");
        assertEquals("7", client.getResponseHeader("Content-Length"));
    }

    @Test
    void returnBodyContent() throws IOException {
        HttpClient client = new HttpClient("urlecho.appspot.com", 80, "/echo?body=Jeg+har+vondt+i+Maven");
        assertEquals("Jeg har vondt i Maven", client.getResponseBody());
    }
}