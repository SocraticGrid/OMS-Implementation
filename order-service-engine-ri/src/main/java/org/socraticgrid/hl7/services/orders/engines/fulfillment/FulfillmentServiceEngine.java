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
package org.socraticgrid.hl7.services.orders.engines.fulfillment;

import java.util.List;

import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.accessclients.orderfulfillment.FulfillmentServiceSE;
import org.socraticgrid.hl7.services.orders.exceptions.OrderingException;
import org.socraticgrid.hl7.services.orders.interfaces.FulfillmentIFace;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.Promise;
import org.socraticgrid.hl7.services.orders.model.Result;
import org.socraticgrid.hl7.services.orders.model.ResultAgumentation;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.requirements.Requirement;
import org.socraticgrid.hl7.services.orders.model.status.CancellationStatus;
import org.socraticgrid.hl7.services.orders.model.status.VerifyStatus;


public class FulfillmentServiceEngine implements FulfillmentIFace {
	private final Logger logger = LoggerFactory.getLogger(FulfillmentServiceEngine.class);
	
	private String endpoint;

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public CancellationStatus cancelOrder(Identifier orderId) {

		FulfillmentServiceSE ss = new FulfillmentServiceSE();

		FulfillmentIFace port = ss.getFulfillmentPort();
		((BindingProvider) port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		logger.debug("SEI calling cancelOrder ");
		CancellationStatus status = port.cancelOrder(orderId);
		logger.debug("cancelOrder called be SEI");
		return status;
	}

	@Override
	public <T extends Order> OrderModel<T> getProposedOrderReplacement(
			Identifier orderId) {

		FulfillmentServiceSE ss = new FulfillmentServiceSE();

		FulfillmentIFace port = ss.getFulfillmentPort();
		((BindingProvider) port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

		logger.debug("SEI calling getProposedOrderReplacement");
		OrderModel<T> out = port.getProposedOrderReplacement(orderId);
		logger.debug("getProposedOrderReplacement called by SEI");
		return out;
	}

	@Override
	public List<ResultAgumentation> getResultAgumentatons(Identifier resultId) {

		FulfillmentServiceSE ss = new FulfillmentServiceSE();

		FulfillmentIFace port = ss.getFulfillmentPort();
		((BindingProvider) port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		
		List<ResultAgumentation> out;
		
		logger.debug("SEI calling getResultAgumentatons");
		out = port.getResultAgumentatons(resultId);
		logger.debug("getResultAgumentatons called by SEI");
		return out;
		

	}

	@Override
	public <T extends Order> Promise requestFulfillment(OrderModel<T> order) {

		
		FulfillmentServiceSE ss = new FulfillmentServiceSE();

		FulfillmentIFace port = ss.getFulfillmentPort();
		logger.debug("Seeing end point to "+endpoint);
		((BindingProvider) port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

		logger.debug("Calling SEI");
		Promise promise = port.requestFulfillment(order);
		logger.debug("Called SEI");

		return promise;
	}

	@Override
	public Result retrieveResultByResultId(Identifier resultId) {

		FulfillmentServiceSE ss = new FulfillmentServiceSE();

		FulfillmentIFace port = ss.getFulfillmentPort();
		((BindingProvider) port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		logger.debug("SEI calling retrieveResultByResultId");
		Result result = port.retrieveResultByResultId(resultId);
		logger.debug("retrieveResultByResultId called by SEI");
		return result;
	}

	@Override
	public List<Result> retrieveResultByOrderId(Identifier orderId) {

		FulfillmentServiceSE ss = new FulfillmentServiceSE();

		FulfillmentIFace port = ss.getFulfillmentPort();
		((BindingProvider) port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		
		logger.debug("SEI Calling retrieveResultByOrderId");
		List<Result> out = port.retrieveResultByOrderId(orderId);
		logger.debug("retrieveResultByOrderId called be SEI");
		return out;
	}
	
	
	@Override
	public Promise updateOrderRequirements(Identifier orderId,
			List<Requirement> requirements) {
		
		FulfillmentServiceSE ss = new FulfillmentServiceSE();

		FulfillmentIFace port = ss.getFulfillmentPort();
		((BindingProvider) port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		
		logger.debug("SEI Calling updateOrderRequirements");
		Promise out = port.updateOrderRequirements(orderId,requirements);
		logger.debug("updateOrderRequirements called be SEI");
		return out;
	}

	@Override
	public VerifyStatus verifyOrderRequirement(Identifier orderId,
			Requirement requirement) throws OrderingException {
		
		FulfillmentServiceSE ss = new FulfillmentServiceSE();

		FulfillmentIFace port = ss.getFulfillmentPort();
		((BindingProvider) port).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		
		logger.debug("SEI Calling updateOrderRequirements");
		VerifyStatus out = port.verifyOrderRequirement(orderId,requirement);
		logger.debug("updateOrderRequirements called be SEI");
		return out;
	}
}
