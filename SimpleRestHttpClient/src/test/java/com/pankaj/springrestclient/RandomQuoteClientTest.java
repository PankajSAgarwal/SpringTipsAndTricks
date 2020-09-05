package com.pankaj.springrestclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import static org.junit.jupiter.api.Assertions.*;


@RestClientTest(RandomQuoteClient.class)
class RandomQuoteClientTest {
    @Autowired
    private RandomQuoteClient randomQuoteClient;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;
    

    @Test
    public void shouldReturnRandomQuote() {
        String result = """
                        {
                          "success": "string",
                          "contents": {
                            "quotes": [
                              {
                                "author": "string",
                                "quote": "Lorem ipsum",
                                "tags": [
                                  "string"
                                ],
                                "id": "string",
                                "image": "string",
                                "length": 0
                              }
                            ]
                          }
                        }             
                """;

        this.mockRestServiceServer
                .expect(MockRestRequestMatchers.requestTo("/qod"))
                .andRespond(MockRestResponseCreators.withSuccess(result, MediaType.APPLICATION_JSON));

        String response = randomQuoteClient.getRandomQuote();
        System.out.println(response);

        assertEquals("Lorem ipsum",response);

    }

    @Test
    public void shouldFailInCaseOfRemoteSystemBeingDown(){
        this.mockRestServiceServer
                .expect(MockRestRequestMatchers.requestTo("/qod"))
                .andRespond(MockRestResponseCreators.withServerError());
        assertThrows(RuntimeException.class, randomQuoteClient::getRandomQuote);
    }
}