For example, a SOAP API `http(s)://GATEWAY_HOST:GATEWAY_PORT/soap?countryName=France` with the following `{{ .Plugin.ID }}`policy SOAP envelope content:

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
Will give you the ISO country code for ```France```.

## ⚠️ Security Warning: XML Injection Prevention
**Important**: When using the REST-to-SOAP policy, be aware of potential XML injection vulnerabilities. User input embedded directly into SOAP envelopes without proper escaping can expose your API to security risks.

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

Without escaping, this could break your SOAP structure:
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