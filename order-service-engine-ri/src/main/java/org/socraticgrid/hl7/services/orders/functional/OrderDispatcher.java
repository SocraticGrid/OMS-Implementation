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
package org.socraticgrid.hl7.services.orders.functional;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.exceptions.OrderingException;
import org.socraticgrid.hl7.services.orders.interfaces.FulfillmentIFace;
import org.socraticgrid.hl7.services.orders.internal.interfaces.OrderManagerIFace;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.Promise;
import org.socraticgrid.hl7.services.orders.model.Result;
import org.socraticgrid.hl7.services.orders.model.operationresults.CancelOrderResult;
import org.socraticgrid.hl7.services.orders.model.operationresults.ChangeOrderResult;
import org.socraticgrid.hl7.services.orders.model.primatives.Code;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.requirements.EndorsementRequirement;
import org.socraticgrid.hl7.services.orders.model.requirements.Requirement;
import org.socraticgrid.hl7.services.orders.model.requirements.RequirementStatusCode;
import org.socraticgrid.hl7.services.orders.model.status.CancellationStatus;
import org.socraticgrid.hl7.services.orders.model.status.ChangeStatus;
import org.socraticgrid.hl7.services.orders.model.status.CreateStatus;
import org.socraticgrid.hl7.services.orders.model.status.UpdateStatus;
import org.socraticgrid.hl7.services.orders.model.status.VerifyStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderDispatcher {
	private final Logger logger = LoggerFactory
			.getLogger(OrderDispatcher.class);

	private final static String fulfillmentOrgTag = "Fulfillment";
	@Autowired
	ServiceRegistry registry;

	@Autowired
	OrderManagerIFace orderManager;

	public CreateStatus dispatchOrder(OrderModel<? extends Order> order) {

		FulfillmentIFace engine;

		logger.debug("Entering dispatch Order");
		// Determine which fulfillment engine to call

		// Fetch Engine
		String orderType = order.getType().getClass().getName();
		logger.debug("Order Type = "+orderType);
		// TODO - Make this section process more intelligent
		engine = getEngineForType(orderType);
		// FulfillmentIntf engine;
		if (engine == null) {
			return CreateStatus.Invalid;
		}
		
		// Call Engine
		Promise promise = null;
		logger.debug("requesting fulfillment");
		try {
			promise = engine.requestFulfillment(order);
		
		} catch (Throwable e) {
			logger.error("Error attempting to fulfill a " + orderType, e);
		}
		
		if (promise != null) {
			// Ok now we need to update the order if there and evaluate the
			// order status
			logger.debug("Updating Order Status");
			
			order.getType().setStatus(promise.getStatus());
			// We need to see if any requirements were asserted by
			// fulfillment and if so added them to the order
			if (promise.getRequirements().isEmpty() == false) {
				logger.debug("promise has requirements");
				Iterator<Requirement> itr = promise.getRequirements()
						.iterator();
				while (itr.hasNext()) {
					Requirement req = itr.next();
					// TODO In a more complex compound model the origination
					// would be a more complicated structure
					req.setOrignator(fulfillmentOrgTag);
					order.getType().getRequirements().add(req);
				}
			}
			
			// We also need to save the promise and update order
			// requirements
			Identifier identifier = order.getType().getOrderIdentity();
			orderManager.saveOrderPromise(identifier, promise);
			orderManager.updateOrder(order);
		}
		else
		{
		
			Code status = new Code();
			status.setCodeSystem("OrderService");
			status.setCode("AwaitingDispatch");
			order.getType().setStatus(status);
			orderManager.updateOrder(order);			
			logger.error("Error no promiss returned");
			
			//TODO remove this Temporary Hack That adds an Endorsement requirement
			logger.debug("Added Endorsement requirement");
			EndorsementRequirement req =  new EndorsementRequirement();
			req.setOrignator(fulfillmentOrgTag);
			order.getType().getRequirements().add(req);
			orderManager.updateOrder(order);
			
		}
		// Save Promise
		logger.debug("fulfillment request done");

		return CreateStatus.Sucessful;
	}

	public FulfillmentIFace getEngineForType(String type) {
		// Check Service Registry
		FulfillmentIFace engine = registry.getService(type);
		if (engine == null) {
			logger.warn("Attempt to create an unknown Order type: " + type);
		}
		return engine;
	}

	public UpdateStatus updateOrderRequirements(Identifier orderId,
			List<Requirement> requirements) throws OrderingException {
		UpdateStatus status = UpdateStatus.Sucessful;

		// Validate the Order Id
		OrderModel<? extends Order> orderModel = orderManager
				.retrieveOrder(orderId);
		if (orderModel == null) {
			throw new OrderingException(
					"********** OrderModel cannot be NULL!!!");
		}

		List<Requirement> orderReq = orderModel.getType().getRequirements();

		LinkedList<Requirement> fulfillmentReq = new LinkedList<>();

		// Check the requirements to see if any originated with fulfillment
		Iterator<Requirement> itr = requirements.iterator();
		while (itr.hasNext()) {
			Requirement res = itr.next();
			if (res.getOrignator().compareTo(fulfillmentOrgTag) == 0) {
				fulfillmentReq.add(res);
			}

			if (res.getStatus() == RequirementStatusCode.Removed) {

			}
			// Update in order
			int index = orderReq.indexOf(res);
			if (index == -1) {
				if (res.getStatus() != RequirementStatusCode.Removed) {
					orderReq.add(res);
				}
			} else {
				if (res.getStatus() != RequirementStatusCode.Removed) {
					// We found and eqv. replace with the update.
					orderReq.set(index, res);
				} else {
					orderReq.remove(index);
				}
			}
		}

		if (!(fulfillmentReq.isEmpty())) {
			// We tell the fulfiller about only their requirements being
			// updated.
			FulfillmentIFace engine = getEngineForType(orderModel.getType()
					.getClass().getName());
			try {
				Promise promise = engine.updateOrderRequirements(orderId,
						fulfillmentReq);
				orderModel.getType().setStatus(promise.getStatus());

				Iterator<Requirement> prmReqItr = promise.getRequirements()
						.iterator();
				while (prmReqItr.hasNext()) {
					Requirement req = prmReqItr.next();
					// Make sure request has the correct originator
					req.setOrignator(fulfillmentOrgTag);

					if (req.getStatus() == RequirementStatusCode.Pending) {
						status = UpdateStatus.Failed;
					} else if (req.getStatus() == RequirementStatusCode.Removed) {
						int index = orderReq.indexOf(req);
						if (index != -1) {
							orderReq.remove(index);
						}
					} else {
						int index = orderReq.indexOf(req);
						if (index == -1) {
							orderReq.add(req);
						} else {
							orderReq.set(index, req);
						}
					}
				}
			} catch (Throwable e) {
				// TODO add Logging
				status = UpdateStatus.Failed;
			}

		}

		// TODO Update the order model

		return status;
	}

	public VerifyStatus verifyOrderRequirement(Identifier orderId,
			Requirement requirement) throws OrderingException {
		OrderModel<? extends Order> orderModel = orderManager
				.retrieveOrder(orderId);
		if (orderModel == null) {
			throw new OrderingException(
					"********** OrderModel cannot be NULL!!!");
		}
		VerifyStatus status = VerifyStatus.Valid;

		if (requirement.getOrignator().compareTo(fulfillmentOrgTag) == 0) {
			FulfillmentIFace engine = getEngineForType(orderModel.getType()
					.getClass().getName());
			status = engine.verifyOrderRequirement(orderId, requirement);
		}

		return status;
	}
	
	/**
	 * 
	 * @param orderId
	 * @return
	 * @throws OrderingException
	 */
	public CancelOrderResult cancelOrder(Identifier orderId) throws OrderingException{
		
		CancelOrderResult result = new CancelOrderResult();
		result.setOrderIdentity(orderId);
		
		OrderModel<? extends Order> orderModel = orderManager
				.retrieveOrder(orderId);
		
		if (orderModel == null) {
			throw new OrderingException("Requested Order does not exist for cancellation.");
		}
		FulfillmentIFace engine = getEngineForType(orderModel.getType()
				.getClass().getName());
		
		CancellationStatus status = engine.cancelOrder(orderId); 
		if(status==CancellationStatus.Sucessful){
			if(orderModel.getType()!=null && orderModel.getType().getStatus()!=null){
				orderModel.getType().getStatus().setCode("Cancelled");
				orderManager.updateOrder(orderModel);
			} 
		} 
		result.setStatus(status);
		
		return result;
		
	}
	/**
	 * 
	 * @param orderId
	 * @param order
	 * @return
	 * @throws OrderingException
	 */
	public <T extends Order> ChangeOrderResult changeOrder(Identifier orderId,
			OrderModel<T> order) throws OrderingException {
		ChangeOrderResult result = new ChangeOrderResult();
		result.setOrderIdentity(orderId);
		ChangeStatus status=null;
		OrderModel<? extends Order> orderModel = orderManager
				.retrieveOrder(orderId);
		
		if (orderModel == null) {
			throw new OrderingException("Requested Order does not exist for change.");
		}
		
		if(orderModel.getType()!=null && orderModel.getType().getStatus()!=null){
			if("Completed".equalsIgnoreCase(orderModel.getType().getStatus().getCode())){ 
				logger.debug("Order "+orderId+" not updated as the status of order is completed.");
				status= ChangeStatus.AlreadyComplete;
			}
			orderModel.getType().getStatus().setCode("Changed");
			orderManager.updateOrder(order);
			status= ChangeStatus.Sucessful;
		} 
		result.setStatus(status);
		return result;
		
	}

	public List<Result> retrieveResults(Identifier resultId) throws OrderingException {
		OrderModel<? extends Order> orderModel = orderManager
				.retrieveOrder(resultId);
		
		if (orderModel == null) {
			throw new OrderingException("Requested Order does not exist for cancellation.");
		}
		FulfillmentIFace engine = getEngineForType(orderModel.getType()
				.getClass().getName());
		
		return engine.retrieveResultByOrderId(resultId);
	}
}
