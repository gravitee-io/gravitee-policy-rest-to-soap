package com.graviteesource.policy.rest2soap.swagger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.policy.api.swagger.Policy;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author Eric LELEU (eric.leleu at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class RestToSoapOAIOperationVisitorTest {

    private RestToSoapOAIOperationVisitor visitor = new RestToSoapOAIOperationVisitor();

    @Test
    public void operationWithoutExtension() {
        Operation operationMock = mock(Operation.class);
        when(operationMock.getExtensions()).thenReturn(null);
        Optional<Policy> policy = visitor.visit(mock(OpenAPI.class), operationMock);
        assertFalse(policy.isPresent());
    }

    @Test
    public void operationWithoutSoapEnvelope() {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put(RestToSoapOAIOperationVisitor.SOAP_EXTENSION_ACTION, "someaction");
        Operation operationMock = mock(Operation.class);
        when(operationMock.getExtensions()).thenReturn(extensions);
        Optional<Policy> policy = visitor.visit(mock(OpenAPI.class), operationMock);
        assertFalse(policy.isPresent());
    }

    @Test
    public void operationWithoutSoapAction() throws Exception {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put(RestToSoapOAIOperationVisitor.SOAP_EXTENSION_ENVELOPE, "<soap:envelope></soap:envelope>");
        Operation operationMock = mock(Operation.class);
        when(operationMock.getExtensions()).thenReturn(extensions);
        Optional<Policy> policy = visitor.visit(mock(OpenAPI.class), operationMock);
        assertTrue(policy.isPresent());
        String configuration = policy.get().getConfiguration();
        assertNotNull(configuration);
        HashMap readConfig = new ObjectMapper().readValue(configuration, HashMap.class);
        assertEquals(extensions.get(RestToSoapOAIOperationVisitor.SOAP_EXTENSION_ENVELOPE), readConfig.get("envelope"));
        assertNull(extensions.get(RestToSoapOAIOperationVisitor.SOAP_EXTENSION_ACTION));
    }

    @Test
    public void operationWithSoapData() throws Exception {
        Map<String, Object> extensions = new HashMap<>();
        extensions.put(RestToSoapOAIOperationVisitor.SOAP_EXTENSION_ENVELOPE, "<soap:envelope></soap:envelope>");
        extensions.put(RestToSoapOAIOperationVisitor.SOAP_EXTENSION_ACTION, "someaction");
        Operation operationMock = mock(Operation.class);
        when(operationMock.getExtensions()).thenReturn(extensions);
        Optional<Policy> policy = visitor.visit(mock(OpenAPI.class), operationMock);
        assertTrue(policy.isPresent());
        String configuration = policy.get().getConfiguration();
        assertNotNull(configuration);
        HashMap readConfig = new ObjectMapper().readValue(configuration, HashMap.class);
        assertEquals(extensions.get(RestToSoapOAIOperationVisitor.SOAP_EXTENSION_ENVELOPE), readConfig.get("envelope"));
        assertEquals(extensions.get(RestToSoapOAIOperationVisitor.SOAP_EXTENSION_ACTION), readConfig.get("soapAction"));
    }
}
