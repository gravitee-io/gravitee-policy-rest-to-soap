package com.graviteesource.policy.rest2soap.el;

import com.graviteesource.policy.rest2soap.RestToSoapTransformerPolicy;
import io.gravitee.common.util.MultiValueMap;
import io.gravitee.gateway.api.ExecutionContext;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class EvaluableRequest extends io.gravitee.gateway.api.el.EvaluableRequest {

    private final MultiValueMap<String, String> parameters;

    public EvaluableRequest(ExecutionContext context, String content) {
        super(context.request(), content);
        this.parameters =
            context.getAttribute(RestToSoapTransformerPolicy.QUERY_PARAMS_ATTRIBUTE) != null
                ? (MultiValueMap<String, String>) context.getAttribute(RestToSoapTransformerPolicy.QUERY_PARAMS_ATTRIBUTE)
                : super.getParams();
    }

    @Override
    public MultiValueMap<String, String> getParams() {
        return parameters;
    }
}
