package com.graviteesource.policy.rest2soap.swagger;

import io.gravitee.policy.api.swagger.Policy;
import io.gravitee.policy.api.swagger.v2.SwaggerOperationVisitor;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import java.util.Optional;

/**
 * @author Eric LELEU (eric.leleu at graviteesource.com)
 * @author GraviteeSource Team
 */
public class RestToSoapSwaggerOperationVisitor implements SwaggerOperationVisitor {

    @Override
    public Optional<Policy> visit(Swagger swagger, Operation operation) {
        // Vendor extension not available in swagger specification
        return Optional.empty();
    }
}
