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

import org.socraticgrid.hl7.services.orders.interfaces.OrderCatalogManagementIFace;



/**
 * This class was Originally generated by Apache CXF 2.7.8
 * Modified by Jerry Goodnough
 * 
 */
@WebServiceClient(name = "OrderCatalogManagementServiceService", 
                  wsdlLocation = "classpath:wsdl/OrderCatalogManagementService.wsdl",
                  targetNamespace = "org.socraticgrid.hl7.services.orders") 
public class OrderCatalogManagementServiceSE extends Service {

    public final static URL WSDL_LOCATION;

 

    public final static QName SERVICE = new QName("org.socraticgrid.hl7.services.orders", "OrderCatalogManagementServiceService");
    public final static QName OrderCatalogManagementPort = new QName("org.socraticgrid.hl7.services.orders", "ordercatalogmanagementPort");
    
    static {
        URL url = null;
        url = OrderCatalogManagementServiceSE.class.getClassLoader().getResource("wsdl/OrderCatalogQueryService.wsdl");
        WSDL_LOCATION = url;
    }

    public OrderCatalogManagementServiceSE(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public OrderCatalogManagementServiceSE(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public OrderCatalogManagementServiceSE() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public OrderCatalogManagementServiceSE(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public OrderCatalogManagementServiceSE(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public OrderCatalogManagementServiceSE(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     *
     * @return
     *     returns OrderWorkflowIFace
     */
    @WebEndpoint(name = "ordercatalogmanagementPort")
    public OrderCatalogManagementIFace getOrderCatalogManagementPort() {
        return super.getPort(OrderCatalogManagementPort, OrderCatalogManagementIFace.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns OrderWorkflowIFace
     */
    @WebEndpoint(name = "ordercatalogmanagementPort")
    public OrderCatalogManagementIFace getOrderCatalogManagementPort(WebServiceFeature... features) {
        return super.getPort(OrderCatalogManagementPort, OrderCatalogManagementIFace.class, features);
    }

}
