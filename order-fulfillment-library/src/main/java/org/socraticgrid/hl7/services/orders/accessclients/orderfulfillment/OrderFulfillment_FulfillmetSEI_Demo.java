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
package org.socraticgrid.hl7.services.orders.accessclients.orderfulfillment;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.socraticgrid.hl7.services.orders.interfaces.FulfillmentIFace;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.Promise;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.types.order.MedicationOrder;


public final class OrderFulfillment_FulfillmetSEI_Demo {

	private static final QName SERVICE_NAME = new QName(
			"org.socraticgrid.hl7.services.orders.clients",
			"FulfillmentServiceService");

	private OrderFulfillment_FulfillmetSEI_Demo() {
	}

	public static void main(String args[]) throws java.lang.Exception {
		URL wsdlURL = FulfillmentServiceSE.WSDL_LOCATION;
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

		String endpoint = "http://172.31.5.68:8080/OrderFulfillment/orderfulfillmentclient";
		
		FulfillmentServiceSE ss = new FulfillmentServiceSE(wsdlURL,
				SERVICE_NAME);
		
		FulfillmentIFace port = ss.getFulfillmentPort();
		((BindingProvider)port).getRequestContext().put(
        	    BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
        	    endpoint);
		
		//Example requestFullfillment Message
		{
			System.out.println("Invoking requestFullfillment...");
			OrderModel<MedicationOrder> order = new OrderModel<MedicationOrder>(new MedicationOrder()); 
			order.getType().setOrderTime(new Date());
			order.getType().getOrderDetails().setMedication("Amed");
			Identifier orderId = new Identifier(); 
			orderId.setValue("1010992");
			orderId.setSystem("Test");
			order.getType().setOrderIdentity(orderId);
			Promise promise= port.requestFulfillment(order);
			System.out.println("requestFullfillment.result=" + promise);

		}
		/*
		//Example retrieveResultByResultId Message
		{
			System.out.println("Invoking retrieveResultByResultId...");
			Identifier resultId = new Identifier();
			resultId.setValue("MyResult");
			
			Result out = port.retrieveResultByResultId(resultId);
			
			System.out.println("retrieveResultByResultId.result="
					+ out);
		}
		//Example retrieveResultByOrderId Message
		{
			System.out.println("Invoking retrieveResultByResultId...");
			Identifier resultId = new Identifier();
			resultId.setValue("MyResult");
			
			Result out = port.retrieveResultByResultId(resultId);
			
			System.out.println("retrieveResultByResultId.result="
					+ out);
		}
		//Example retrieveResultByOrderId Message
		{
			System.out.println("Invoking retrieveResultByResultIdretrieveResultByResultId...");
			Identifier resultId = new Identifier();
			resultId.setValue("MyResult");
			
			List<Result> out = port.retrieveResultByOrderId(resultId);
			
			System.out.println("retrieveResultByOrderId.result="
					+ out);
		}
*/
		
		//public CancellationStatus cancelOrder(Identifier orderId);

		//public <T extends Order> OrderModel<T> getProposedOrderReplacement(
		//		Identifier orderId);

		//public void getResultAgumentatons(Identifier resultId) ;
		
		System.exit(0);
	}

}
