package com.graviteesource.policy.rest2soap.configuration;

import io.gravitee.policy.api.PolicyConfiguration;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class SoapTransformerPolicyConfiguration implements PolicyConfiguration {

    private String envelope;

    private String soapAction;

    private String charset;

    private boolean preserveQueryParams = false;

    private boolean stripPath = false;

    public String getEnvelope() {
        return envelope;
    }

    public void setEnvelope(String envelope) {
        this.envelope = envelope;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isPreserveQueryParams() {
        return preserveQueryParams;
    }

    public void setPreserveQueryParams(boolean preserveQueryParams) {
        this.preserveQueryParams = preserveQueryParams;
    }

    public boolean isStripPath() {
        return stripPath;
    }

    public void setStripPath(boolean stripPath) {
        this.stripPath = stripPath;
    }
}
