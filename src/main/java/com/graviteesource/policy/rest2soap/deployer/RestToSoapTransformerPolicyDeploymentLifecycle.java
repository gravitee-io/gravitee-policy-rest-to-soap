package com.graviteesource.policy.rest2soap.deployer;

import io.gravitee.node.api.deployer.AbstractPluginDeploymentLifecycle;

/**
 * @author Kamiel Ahmadpour (kamiel.ahmadpour at graviteesource.com)
 * @author GraviteeSource Team
 */
public class RestToSoapTransformerPolicyDeploymentLifecycle extends AbstractPluginDeploymentLifecycle {

    private static final String REST_TO_SOAP_TRANSFORMER_POLICY = "apim-policy-rest-to-soap";

    @Override
    protected String getFeatureName() {
        return REST_TO_SOAP_TRANSFORMER_POLICY;
    }
}
