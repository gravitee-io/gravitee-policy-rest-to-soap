package com.graviteesource.policy.rest2soap.swagger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.graviteesource.policy.rest2soap.configuration.SoapTransformerPolicyConfiguration;
import io.gravitee.policy.api.swagger.Policy;
import io.gravitee.policy.api.swagger.v3.OAIOperationVisitor;
import java.util.Map;
import java.util.Optional;

/**
 * @author Eric LELEU (eric.leleu at graviteesource.com)
 * @author GraviteeSource Team
 */
public class RestToSoapOAIOperationVisitor implements OAIOperationVisitor {

    public static final String SOAP_EXTENSION_ENVELOPE = "x-graviteeio-soap-envelope";
    public static final String SOAP_EXTENSION_ACTION = "x-graviteeio-soap-action";

    private final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public Optional<Policy> visit(io.swagger.v3.oas.models.OpenAPI openAPI, io.swagger.v3.oas.models.Operation operation) {
        Map<String, Object> extensions = operation.getExtensions();

        if (extensions != null && extensions.containsKey(SOAP_EXTENSION_ENVELOPE)) {
            SoapTransformerPolicyConfiguration configuration = new SoapTransformerPolicyConfiguration();
            try {
                Policy policy = new Policy();
                policy.setName("rest-to-soap");
                configuration.setEnvelope((String) extensions.get(SOAP_EXTENSION_ENVELOPE));
                configuration.setSoapAction((String) extensions.get(SOAP_EXTENSION_ACTION));
                configuration.setCharset("");
                configuration.setPreserveQueryParams(false);
                policy.setConfiguration(mapper.writeValueAsString(configuration));
                return Optional.of(policy);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }
}
