/* 
 * Copyright 2015 Cognitive Medical Systems, Inc (http://www.cognitivemedciine.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.socraticgrid.hl7.services.orders.accessclients.orderservice;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import org.socraticgrid.hl7.services.orders.interfaces.FulfillmentUpdateIFace;
import org.socraticgrid.hl7.services.orders.interfaces.OrderWorkflowIFace;

/**
 * This class was Originally generated by Apache CXF 2.7.8
 * Modified by Jerry Goodnough
 * 
 */
@WebServiceClient(name = "FulfillmentUpdateServiceService", 
                  wsdlLocation = "classpath:wsdl/FulfillmentUpdateService.wsdl",
                  targetNamespace = "org.socraticgrid.hl7.services.orders") 
public class FulfillmentUpdateServiceSE extends Service {

    public final static URL WSDL_LOCATION;

 

    public final static QName SERVICE = new QName("org.socraticgrid.hl7.services.orders", "FulfillmentUpdateServiceService");
    public final static QName FulfillmentUpdatePort = new QName("org.socraticgrid.hl7.services.orders", "fulfillmentupdatePort");
    
    static {
        URL url = null;
        url = FulfillmentUpdateServiceSE.class.getClassLoader().getResource("wsdl/FulfillmentUpdateService.wsdl");
        WSDL_LOCATION = url;
    }

    public FulfillmentUpdateServiceSE(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public FulfillmentUpdateServiceSE(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public FulfillmentUpdateServiceSE() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public FulfillmentUpdateServiceSE(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public FulfillmentUpdateServiceSE(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public FulfillmentUpdateServiceSE(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns OrderWorkflowIFace
     */
    @WebEndpoint(name = "fulfillmentupdatePort")
    public FulfillmentUpdateIFace getFulfillmentUpdatePort() {
        return super.getPort(FulfillmentUpdatePort, FulfillmentUpdateIFace.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns FulfillmentUpdateIFace
     */
    @WebEndpoint(name = "fulfillmentupdatePort")
    public FulfillmentUpdateIFace getFulfillmentUpdatePort(WebServiceFeature... features) {
        return super.getPort(FulfillmentUpdatePort, FulfillmentUpdateIFace.class, features);
    }

}
