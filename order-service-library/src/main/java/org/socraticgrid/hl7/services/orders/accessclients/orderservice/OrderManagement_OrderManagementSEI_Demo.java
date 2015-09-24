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


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;



import org.socraticgrid.hl7.services.orders.interfaces.OrderManagementIFace;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.Promise;
import org.socraticgrid.hl7.services.orders.model.operationresults.CreateOrderResult;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.types.order.MedicationOrder;
import org.socraticgrid.hl7.services.orders.model.types.orderitems.MedicationOrderItem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public final class OrderManagement_OrderManagementSEI_Demo {

	private static final QName SERVICE_NAME = new QName(
			"org.socraticgrid.hl7.services.orders",
			"OrderManagementServiceService");

	private OrderManagement_OrderManagementSEI_Demo() {
	}


	
	public static void main(String args[]) throws java.lang.Exception {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
		     "classpath:/OrderServiceLib_TestBeans.xml");
		 
		 
		URL wsdlURL = OrderManagementServiceSE.WSDL_LOCATION;
		if (args.length > 0 && args[0] != null && !"".equals(args[0])) {
			File wsdlFile = new File(args[0]);
			try {
				if (wsdlFile.exists()) {
					wsdlURL = wsdlFile.toURI().toURL();
				} else {
					wsdlURL = new URL(args[0]);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		String endpoint = "http://172.31.5.68:8080/OrderService/ordermanagement";
		
		OrderManagementServiceSE ss = new OrderManagementServiceSE(wsdlURL,
				SERVICE_NAME);
		
		OrderManagementIFace port = ss.getOrderManagementPort();
		((BindingProvider)port).getRequestContext().put(
        	    BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
        	    endpoint);
		
		//Example requestFullfillment Message
		{
			System.out.println("Invoking createOrder...");
			MedicationOrder medOrder = new MedicationOrder();
			medOrder.setOrderTime(new Date());
			//TODO Update the identity to deal with the new collection model
			Identifier id = new Identifier();
			id.setValue("PatientId");
			medOrder.setOrderIdentity(id);

			MedicationOrderItem item = new MedicationOrderItem();
			
			Identifier med = new Identifier();
			med.setValue("Some Code");
			med.setSystem("SomeSystem");
			item.getMedication().add(med);
			
	
			medOrder.getOrdereditems().add(item);
			item.getDispenseQuantity().setUnits("mg");
			item.getDosageQuantity().setValue("12.5");
			OrderModel<MedicationOrder> order = new OrderModel<MedicationOrder>(medOrder); 

			CreateOrderResult result = port.createOrder(order);
			System.out.println("createOrder.result=" + result);

		}
		
	
	
		
		System.exit(0);
	}

}
