
<!-- GENERATED CODE - DO NOT ALTER THIS OR THE FOLLOWING LINES -->
# Rest to SOAP Transformer

[![Gravitee.io](https://img.shields.io/static/v1?label=Available%20at&message=Gravitee.io&color=1EC9D2)](https://download.gravitee.io/#graviteeio-apim/plugins/policies/gravitee-policy-rest-to-soap/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/gravitee-io/gravitee-policy-rest-to-soap/blob/master/LICENSE.txt)
[![Releases](https://img.shields.io/badge/semantic--release-conventional%20commits-e10079?logo=semantic-release)](https://github.com/gravitee-io/gravitee-policy-rest-to-soap/releases)
[![CircleCI](https://circleci.com/gh/gravitee-io/gravitee-policy-rest-to-soap.svg?style=svg)](https://circleci.com/gh/gravitee-io/gravitee-policy-rest-to-soap)

## Overview
You can use the Rest-to-soap policy to expose SOAP backend service as a REST API. The policy passes the SOAP envelope message to the backend service as a POST request. SOAP envelopes support Expression Language to provide dynamic SOAP actions.



## Usage
For example, a SOAP API `http(s)://GATEWAY_HOST:GATEWAY_PORT/soap?countryName=France` with the following `rest-to-soap`policy SOAP envelope content:

``` XML
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope\\\" xmlns:web=\\\"http://www.oorsprong.org/websamples.countryinfo">
   <soap:Header/>
   <soap:Body>
      <web:CountryISOCode>
         <web:sCountryName>{#request.params['countryName']}</web:sCountryName>
      </web:CountryISOCode>
   </soap:Body>
</soap:Envelope>
```
Gives you the ISO country code for ```France```.

## ⚠️ Security Warning: XML Injection Prevention
**Important**: When you use the REST-to-SOAP policy, you must be aware of potential XML injection vulnerabilities. User input embedded directly into SOAP envelopes without proper escaping might expose your API to security risks.

### Risk Example
For the following SOAP envelope property:
``` XML
<soap:Envelope>
  <soap:Body>
    <web:getUserInfo>
      <web:id>{#request.params['userId']}</web:id>
    </web:getUserInfo>
  </soap:Body>
</soap:Envelope>
```

If user input contains XML-like content with the following url:
`http(s)://GATEWAY_HOST:GATEWAY_PORT/soap?userId=1</web:id><web:id>2`

Without escaping, this might break your SOAP structure:
``` XML
<soap:Envelope>
  <soap:Body>
    <web:getUserInfo>
      <web:id>1</web:id><web:id>2</web:id>  <!-- BROKEN XML! -->
    </web:getUserInfo>
  </soap:Body>
</soap:Envelope>
```

### Recommended Solution:
Use the ```#xmlEscape()``` function in your EL expressions to safely escape user input:
``` XML
<soap:Envelope>
  <soap:Body>
    <web:getUserInfo>
      <web:id>{#xmlEscape(#request.params['userId'])}</web:id>
    </web:getUserInfo>
  </soap:Body>
</soap:Envelope>
```
**Result:**
```
<web:id>1&lt;/web:id&gt;&lt;/web:id&gt;2</web:id>
```

## Best Practices
✅ Always use ```{#xmlEscape()}``` for user input in SOAP templates  
✅ Apply escaping to request parameters, headers, and body content  
✅ Consider using the ```xml-threat-protection``` policy for additional security  
❌ Never embed unescaped user input directly in XML/SOAP structures  



## Phases
The `rest-to-soap` policy can be applied to the following API types and flow phases.

### Compatible API types

* `PROXY`

### Supported flow phases:

* Request

## Compatibility matrix
Strikethrough text indicates that a version is deprecated.

| Plugin version| APIM |
| --- | ---  |
|1.x|All supported versions |


## Configuration options


#### 
| Name <br>`json name`  | Type <br>`constraint`  | Mandatory  | Default  | Description  |
|:----------------------|:-----------------------|:----------:|:---------|:-------------|
| Charset<br>`charset`| string|  | | This charset will be appended to the Content-Type header value.|
| SOAP Envelope<br>`envelope`| string| ✅| | SOAP envelope used to invoke WS. (support EL)|
| Preserve Query Parameters<br>`preserveQueryParams`| boolean|  | | Define if the query parameters are propagated to the backend SOAP service.|
| SOAP Action<br>`soapAction`| string|  | | 'SOAPAction' HTTP header send when invoking WS.|
| Strip path<br>`stripPath`| boolean|  | | Strip the path before propagating it to the backend SOAP service.|




## Examples
*Proxy API With Defaults*
```json
{
  "api": {
    "definitionVersion": "V4",
    "type": "PROXY",
    "name": "Rest to SOAP Transformer example API",
    "flows": [
      {
        "name": "Common Flow",
        "enabled": true,
        "selectors": [
          {
            "type": "HTTP",
            "path": "/",
            "pathOperator": "STARTS_WITH"
          }
        ],
        "request": [
          {
            "name": "Rest to SOAP Transformer",
            "enabled": true,
            "policy": "rest-to-soap",
            "configuration":
              {
                "envelope": "<?xml version=\"1.0\"?>\n<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:web=\"http://www.oorsprong.org/websamples.countryinfo\"><soap:Header/><soap:Body><web:ListOfCountryNamesByName/></soap:Body></soap:Envelope>",
                "preserveQueryParams": false,
                "stripPath": false
              }
          }
        ]
      }
    ]
  }
}

```

*Proxy API on Request phase*
```json
{
  "api": {
    "definitionVersion": "V4",
    "type": "PROXY",
    "name": "Rest to SOAP Transformer example API",
    "flows": [
      {
        "name": "Common Flow",
        "enabled": true,
        "selectors": [
          {
            "type": "HTTP",
            "path": "/",
            "pathOperator": "STARTS_WITH"
          }
        ],
        "request": [
          {
            "name": "Rest to SOAP Transformer",
            "enabled": true,
            "policy": "rest-to-soap",
            "configuration":
              {
                "envelope": "<?xml version=\"1.0\"?>\n<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:web=\"http://www.oorsprong.org/websamples.countryinfo\"><soap:Header/><soap:Body><web:ListOfCountryNamesByName/></soap:Body></soap:Envelope>",
                "soapAction": "urn:MyAction",
                "charset": "UTF-8",
                "preserveQueryParams": true,
                "stripPath": false
              }
          }
        ]
      }
    ]
  }
}

```


## Changelog

#### [1.14.1](https://github.com/gravitee-io/gravitee-policy-rest-to-soap/compare/1.14.0...1.14.1) (2023-07-20)


##### Bug Fixes

* update policy description ([5050690](https://github.com/gravitee-io/gravitee-policy-rest-to-soap/commit/5050690ae86c3184ddbd8522135aa79b18ba7085))

### [1.14.0](https://github.com/gravitee-io/gravitee-policy-rest-to-soap/compare/1.13.0...1.14.0) (2023-07-05)


##### Features

* add execution phase ([6fdafc0](https://github.com/gravitee-io/gravitee-policy-rest-to-soap/commit/6fdafc0ecb2b6e6f254be51ef423dd8153231119))

### [1.13.0](https://github.com/gravitee-io/gravitee-policy-rest-to-soap/compare/1.12.0...1.13.0) (2022-01-31)


##### Features

* **headers:** internal HTTP headers refactoring ([76bb145](https://github.com/gravitee-io/gravitee-policy-rest-to-soap/commit/76bb1451005a3410fe87929b6ddabd8acfa67b9c)), closes [gravitee-io/issues#7025](https://github.com/gravitee-io/issues/issues/7025)

