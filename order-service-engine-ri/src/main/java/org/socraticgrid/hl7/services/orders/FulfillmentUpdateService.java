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
package org.socraticgrid.hl7.services.orders;


import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.interfaces.FulfillmentUpdateIFace;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.Result;
import org.socraticgrid.hl7.services.orders.model.ResultAgumentation;
import org.socraticgrid.hl7.services.orders.model.primatives.Code;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.status.UpdateStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@WebService(name = "fulfillmentupdate", targetNamespace = "org.socraticgrid.hl7.services.orders")
public class FulfillmentUpdateService  implements FulfillmentUpdateIFace {

	private final Logger logger = LoggerFactory.getLogger(FulfillmentUpdateService.class);
	
	@Autowired
	@Qualifier("FulfillmentUpdateServiceImpl")
	FulfillmentUpdateIFace fulfillmentUpdateService;
	
	public <T extends Order> boolean proposeOrderReplacement(@WebParam(name = "orderId")Identifier orderId,
			@WebParam(name = "order")OrderModel<T> order) {
	
		return fulfillmentUpdateService.proposeOrderReplacement(orderId, order);
	}


	public boolean submitResultAgmentation(@WebParam(name = "resultIdentifer")Identifier resultId,
			@WebParam(name = "agumentations")List<ResultAgumentation> agumentations) {
		
		return fulfillmentUpdateService.submitResultAgmentation(resultId, agumentations);
	}


	public UpdateStatus updateOrderStatus(@WebParam(name = "orderId")Identifier orderId,@WebParam(name = "status") Code status) {
		
		return fulfillmentUpdateService.updateOrderStatus(orderId, status);
	}


	public UpdateStatus updateResult(@WebParam(name = "orderId")Identifier orderId, @WebParam(name = "result")Result result) {
	
		return fulfillmentUpdateService.updateResult(orderId, result);
	}


	public UpdateStatus updateResultStatus(@WebParam(name = "resultIdentifier")Identifier resultIdentifier, @WebParam(name = "status")Code status) {
	
		return fulfillmentUpdateService.updateResultStatus(resultIdentifier, status);
	}

}
