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
package io.gravitee.policy.rest2soap.el;

import io.gravitee.common.util.MultiValueMap;
import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.policy.rest2soap.RestToSoapTransformerPolicy;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class EvaluableRequest extends io.gravitee.gateway.api.el.EvaluableRequest {

    private final MultiValueMap<String, String> parameters;

    public EvaluableRequest(ExecutionContext context, String content) {
        super(context.request(), content);

        this.parameters = context.getAttribute(RestToSoapTransformerPolicy.QUERY_PARAMS_ATTRIBUTE) != null ?
                (MultiValueMap<String, String>) context.getAttribute(RestToSoapTransformerPolicy.QUERY_PARAMS_ATTRIBUTE) :
                super.getParams();
    }

    @Override
    public MultiValueMap<String, String> getParams() {
        return parameters;
    }
}
