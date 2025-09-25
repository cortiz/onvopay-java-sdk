/*
 * Copyright (c) 2025, Carlos Ortiz
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *  following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *  following disclaimer in the documentation and/or other materials provided with the distribution.
 *  3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 *      products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.github.cortiz.onvopay.api;

import com.github.cortiz.onvopay.exceptions.HttpClientException;
import com.github.cortiz.onvopay.exceptions.OnvoPayException;
import com.github.cortiz.onvopay.models.Address;
import com.github.cortiz.onvopay.models.CreateClient;
import com.github.cortiz.onvopay.utils.HttpClient;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClientsAPITest {

    @Test
    void testCreateClient_Success() throws Exception {
        var mockHttpClient = mock(HttpClient.class);
        var mockResponse = mock(HttpResponse.class);
        var api = new ClientsAPI(mockHttpClient);
        var customer = new CreateClient(new Address("SJ", "CR", "TuanisCloud", "Apt3", "30303", "Cartago"),
                                        "Test User", "myEmail@email.com", "Test Subject 1", "+50612345678", null);
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockResponse.body()).thenReturn("""
                                                       {
                                                       "id": "cl502zv0d0127ebdp3zt27651",
                                                       "address": {
                                                         "city": "San José",
                                                         "country": "CR",
                                                         "line1": null,
                                                         "line2": null,
                                                         "postalCode": "10101",
                                                         "state": "San José"
                                                       },
                                                       "amountSpent": 0,
                                                       "description": "Cliente de prueba",
                                                       "createdAt": "2022-06-12T21:21:10.587Z",
                                                       "email": "myEmail@email.com",
                                                       "lastTransactionAt": null,
                                                       "mode": "test",
                                                       "name": "John Doe",
                                                       "phone": "+50688880000",
                                                       "shipping": {
                                                         "address": {
                                                           "city": null,
                                                           "country": "CR",
                                                           "line1": null,
                                                           "line2": null,
                                                           "postalCode": null,
                                                           "state": null
                                                         },
                                                         "name": "John Doe",
                                                         "phone": null
                                                       },
                                                       "transactionsCount": 0,
                                                       "updatedAt": "2022-06-12T21:21:10.587Z"
                                                     }
                                                     """);
        when(mockHttpClient.post(anyString(), anyString())).thenReturn(mockResponse);
        var newCustomer = api.createClient(customer);
        assertNotNull(newCustomer);
        assertEquals("myEmail@email.com", newCustomer.email());
        assertNotNull(newCustomer.shipping().address());
    }

    @Test
    void testCreateClient_Error_NullCustomer() {
        var mockHttpClient = mock(HttpClient.class);
        var api = new ClientsAPI(mockHttpClient);
        assertThrows(IllegalArgumentException.class, () -> api.createClient(null));
    }

    @Test
    void testCreateClient_Error_400() throws HttpClientException {
        var mockHttpClient = mock(HttpClient.class);
        var mockResponse = mock(HttpResponse.class);
        var api = new ClientsAPI(mockHttpClient);
        var customer = new CreateClient(new Address("SJ", "CR", "TuanisCloud", "Apt3", "30303", "Cartago"),
                                        "Test User", "myEmail@email.com", "Test Subject 1", "+50612345678", null);
        when(mockHttpClient.post(anyString(), anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("""
                                                           {
                                                             "statusCode": 400,
                                                             "message": [
                                                               "address.country must be a valid ISO31661 Alpha2 code"
                                                             ],
                                                             "error": "Bad Request"
                                                           }
                                                     """);
        var ex = assertThrows(OnvoPayException.class, () -> api.createClient(customer));
        assertEquals(400, ex.getStatusCode());
        assertEquals("address.country must be a valid ISO31661 Alpha2 code", ex.getMessages().get(0));
    }

    @Test
    void testCreateClient_Error_401() throws HttpClientException {
        var mockHttpClient = mock(HttpClient.class);
        var mockResponse = mock(HttpResponse.class);
        var api = new ClientsAPI(mockHttpClient);
        var customer = new CreateClient(new Address("SJ", "CR", "TuanisCloud", "Apt3", "30303", "Cartago"),
                                        "Test User", "myEmail@email.com", "Test Subject 1", "+50612345678", null);
        when(mockHttpClient.post(anyString(), anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockResponse.body()).thenReturn("""
                                                       {
                                                           "statusCode": 401,
                                                           "error": "Unauthorized"
                                                       }
                                                     """);
        var ex = assertThrows(OnvoPayException.class, () -> api.createClient(customer));
        assertEquals(401, ex.getStatusCode());
        assertEquals("Unauthorized", ex.getError());
    }

    @Test
    void testCreateClient_Error_403() throws HttpClientException {
        var mockHttpClient = mock(HttpClient.class);
        var mockResponse = mock(HttpResponse.class);
        var api = new ClientsAPI(mockHttpClient);
        var customer = new CreateClient(new Address("SJ", "CR", "TuanisCloud", "Apt3", "30303", "Cartago"),
                                        "Test User", "myEmail@email.com", "Test Subject 1", "+50612345678", null);
        when(mockHttpClient.post(anyString(), anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(403);
        when(mockResponse.body()).thenReturn("""
                                                       {
                                                          "statusCode": 403,
                                                          "message": "The provided API key is not valid.",
                                                          "error": "Forbidden"
                                                       }
                                                     """);
        var ex = assertThrows(OnvoPayException.class, () -> api.createClient(customer));
        assertEquals(403, ex.getStatusCode());
        assertEquals("The provided API key is not valid.", ex.getMessages().get(0));
    }

    @Test
    void testCreateClient_Error_EmptyResponse() throws HttpClientException {
        var mockHttpClient = mock(HttpClient.class);
        var mockResponse = mock(HttpResponse.class);
        var api = new ClientsAPI(mockHttpClient);
        var customer = new CreateClient(new Address("SJ", "CR", "TuanisCloud", "Apt3", "30303", "Cartago"),
                                        "Test User", "myEmail@email.com", "Test Subject 1", "+50612345678", null);
        when(mockHttpClient.post(anyString(), anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockResponse.body()).thenReturn("");
        var ex = assertThrows(OnvoPayException.class, () -> api.createClient(customer));
        assertEquals("Unable to read client response", ex.getMessage());
    }

    @Test
    void testCreateClient_Error_ClientError() throws HttpClientException {
        var mockHttpClient = mock(HttpClient.class);
        var api = new ClientsAPI(mockHttpClient);
        var customer = new CreateClient(new Address("SJ", "CR", "TuanisCloud", "Apt3", "30303", "Cartago"),
                                        "Test User", "myEmail@email.com", "Test Subject 1", "+50612345678", null);
        when(mockHttpClient.post(anyString(), anyString())).thenThrow(new HttpClientException("Mock Error"));
        var ex = assertThrows(OnvoPayException.class, () -> api.createClient(customer));
        assertEquals("Mock Error", ex.getMessage());
    }

    @Test
    void testGetClientByEmail_Success() throws HttpClientException, OnvoPayException {
        var mockHttpClient = mock(HttpClient.class);
        var mockResponse = mock(HttpResponse.class);
        var api = new ClientsAPI(mockHttpClient);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("""
                                                     {
                                                         "data": [
                                                                   {
                                                                     "id": "cl502zv0d0127ebdp3zt27651",
                                                                     "address": {
                                                                       "city": "San José",
                                                                       "country": "CR",
                                                                       "line1": null,
                                                                       "line2": null,
                                                                       "postalCode": "10101",
                                                                       "state": "San José"
                                                                     },
                                                                     "amountSpent": 0,
                                                                     "description": "Cliente de prueba",
                                                                     "createdAt": "2022-06-12T21:21:10.587Z",
                                                                     "email": "test_customer@onvopay.com",
                                                                     "lastTransactionAt": null,
                                                                     "mode": "test",
                                                                     "name": "John Doe",
                                                                     "phone": "+50688880000",
                                                                     "shipping": {
                                                                       "address": {
                                                                         "city": null,
                                                                         "country": "CR",
                                                                         "line1": null,
                                                                         "line2": null,
                                                                         "postalCode": null,
                                                                         "state": null
                                                                       },
                                                                       "name": "John Doe",
                                                                       "phone": null
                                                                     },
                                                                     "transactionsCount": 0,
                                                                     "updatedAt": "2022-06-12T21:21:10.587Z"
                                                                   }
                                                                 ],
                                                         "meta": {
                                                             "total": 15,
                                                             "limit": 10,
                                                             "pages": 2,
                                                             "cursorNext": "cmfwnvxdpq0w7k02dv0q4x6wo",
                                                             "cursorBefore": "cmfwo6hfkq16fk02d176k4c1l"
                                                         }
                                                     }
                                                     """);
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        var customerOptional = api.getClientsByEmail("test_customer@onvopay.com");
        assertNotNull(customerOptional);
        assertFalse(customerOptional.isEmpty());
        var customers = customerOptional.get();
        assertFalse(customers.data().isEmpty());
        assertEquals(1, customers.data().size());
        assertEquals("cl502zv0d0127ebdp3zt27651", customers.data().getFirst().id());
        assertEquals(2, customers.meta().pages());
        assertEquals("cmfwo6hfkq16fk02d176k4c1l", customers.meta().cursorBefore());
    }

    @Test
    void testGetClientByEmail_Error_email() throws HttpClientException, OnvoPayException {
        var api = new ClientsAPI(mock(HttpClient.class));
        assertTrue(api.getClientsByEmail(null).isEmpty());
        assertTrue(api.getClientsByEmail("").isEmpty());
    }

    @Test
    void testGetClientByEmail_Error_not200_BadResponse() throws HttpClientException, OnvoPayException {
        var mockHttpClient = mock(HttpClient.class);
        var mockResponse = mock(HttpResponse.class);
        var api = new ClientsAPI(mockHttpClient);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("""
                                                     """);
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        var ex = assertThrows(OnvoPayException.class, () -> {
            api.getClientsByEmail("email@email.com");
        });
        assertEquals("Unable to read onvo error response", ex.getMessage());
    }

    @Test
    void testGetClientByEmail_Error_403() throws HttpClientException, OnvoPayException {
        var mockHttpClient = mock(HttpClient.class);
        var mockResponse = mock(HttpResponse.class);
        var api = new ClientsAPI(mockHttpClient);
        when(mockResponse.statusCode()).thenReturn(403);
        when(mockResponse.body()).thenReturn("""
                                                      {
                                                          "statusCode": 403,
                                                          "message": "The provided API key is not valid.",
                                                          "error": "Forbidden"
                                                       }
                                                     """);
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        var ex = assertThrows(OnvoPayException.class, () -> {
            api.getClientsByEmail("email@email.com");
        });
        assertEquals("The provided API key is not valid.", ex.getMessages().getFirst());
        assertEquals(403, ex.getStatusCode());
    }

    @Test
    void testGetClientByEmail_Error_bad_response() throws HttpClientException, OnvoPayException {
        var mockHttpClient = mock(HttpClient.class);
        var mockResponse = mock(HttpResponse.class);
        var api = new ClientsAPI(mockHttpClient);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("""
                                                     
                                                     """);
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        var ex = assertThrows(OnvoPayException.class, () -> {
            api.getClientsByEmail("email@email.com");
        });
        assertEquals("Unable to read client response", ex.getMessage());
    }

    @Test
    void testGetClients_Error_limits() {
        var api = new ClientsAPI(mock(HttpClient.class));
        assertThrows(IllegalArgumentException.class, () -> {
            api.getClients(Integer.MAX_VALUE * -1, null, null);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            api.getClients(Integer.MAX_VALUE, null, null);
        });
    }
}