/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.policy.rest2soap;

import io.gravitee.common.http.HttpMethod;
import io.gravitee.common.http.HttpStatusCode;
import io.gravitee.common.http.MediaType;
import io.gravitee.common.util.LinkedMultiValueMap;
import io.gravitee.el.exceptions.ELNullEvaluationException;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.api.Request;
import io.gravitee.gateway.api.Response;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.http.stream.TransformableRequestStreamBuilder;
import io.gravitee.gateway.api.stream.ReadWriteStream;
import io.gravitee.policy.api.PolicyChain;
import io.gravitee.policy.api.annotations.OnRequest;
import io.gravitee.policy.api.annotations.OnRequestContent;
import io.gravitee.policy.rest2soap.configuration.SoapTransformerPolicyConfiguration;
import io.gravitee.policy.rest2soap.el.EvaluableRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class RestToSoapTransformerPolicy {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RestToSoapTransformerPolicy.class);

    private static final String SOAP_ACTION_HEADER = "SOAPAction";

    public static final String QUERY_PARAMS_ATTRIBUTE = ExecutionContext.ATTR_PREFIX + "rest-to-soap.query-parameters";

    /**
     * SOAP transformer configuration
     */
    private final SoapTransformerPolicyConfiguration soapTransformerPolicyConfiguration;

    public RestToSoapTransformerPolicy(SoapTransformerPolicyConfiguration soapTransformerPolicyConfiguration) {
        this.soapTransformerPolicyConfiguration = soapTransformerPolicyConfiguration;
    }

    @OnRequest
    public void onRequest(Request request, Response response, ExecutionContext executionContext, PolicyChain policyChain) {
        // Force method to POST for SOAP requests
        LOGGER.debug("Override HTTP methods for SOAP invocation");
        executionContext.setAttribute(ExecutionContext.ATTR_REQUEST_METHOD, HttpMethod.POST);

        if (soapTransformerPolicyConfiguration.isStripPath()) {
            executionContext.setAttribute(ExecutionContext.ATTR_REQUEST_ENDPOINT, "");
        }

        if (
            soapTransformerPolicyConfiguration.getSoapAction() != null &&
            !soapTransformerPolicyConfiguration.getSoapAction().trim().isEmpty()
        ) {
            LOGGER.debug("Add a SOAPAction header to invoke SOAP WS: {}", soapTransformerPolicyConfiguration.getSoapAction());
            request.headers().set(SOAP_ACTION_HEADER, soapTransformerPolicyConfiguration.getSoapAction());
        }

        if (!soapTransformerPolicyConfiguration.isPreserveQueryParams()) {
            // We are removing parameters from request, but those one can be used from SOAP envelope template
            // So let's create a copy of them for then being reused later from template engine
            executionContext.setAttribute(QUERY_PARAMS_ATTRIBUTE, new LinkedMultiValueMap<>(request.parameters()));

            request.parameters().clear();
        }

        policyChain.doNext(request, response);
    }

    @OnRequestContent
    public ReadWriteStream onRequestContent(Request request, ExecutionContext executionContext, PolicyChain policyChain) {
        String charset = soapTransformerPolicyConfiguration.getCharset();
        String contentType = (charset == null || charset.isEmpty() ? MediaType.TEXT_XML : MediaType.TEXT_XML + "; charset=" + charset);
        return TransformableRequestStreamBuilder
            .on(request)
            .contentType(contentType)
            .transform(buffer -> {
                executionContext
                    .getTemplateEngine()
                    .getTemplateContext()
                    .setVariable("request", new EvaluableRequest(executionContext, buffer.toString()));

                String soapEnvelope = executionContext.getTemplateEngine().convert(soapTransformerPolicyConfiguration.getEnvelope());

                if (soapEnvelope == null) {
                    policyChain.streamFailWith(
                        io.gravitee.policy.api.PolicyResult.failure(
                            HttpStatusCode.INTERNAL_SERVER_ERROR_500,
                            new ELNullEvaluationException(soapTransformerPolicyConfiguration.getEnvelope()).getMessage()
                        )
                    );
                    return null;
                }

                return Buffer.buffer(soapEnvelope);
            })
            .build();
    }
}
