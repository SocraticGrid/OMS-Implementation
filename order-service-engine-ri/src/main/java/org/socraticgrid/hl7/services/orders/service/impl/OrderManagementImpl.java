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
package org.socraticgrid.hl7.services.orders.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.exceptions.OrderingException;
import org.socraticgrid.hl7.services.orders.functional.EventLogger;
import org.socraticgrid.hl7.services.orders.functional.MessageIdGenerator;
import org.socraticgrid.hl7.services.orders.functional.OrderDispatcher;
import org.socraticgrid.hl7.services.orders.functional.OrderValidator;
import org.socraticgrid.hl7.services.orders.interfaces.OrderManagementIFace;
import org.socraticgrid.hl7.services.orders.internal.interfaces.OrderManagerIFace;
import org.socraticgrid.hl7.services.orders.logging.EventLevel;
import org.socraticgrid.hl7.services.orders.logging.LogEntryType;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.OrderSummary;
import org.socraticgrid.hl7.services.orders.model.Result;
import org.socraticgrid.hl7.services.orders.model.ResultAgumentation;
import org.socraticgrid.hl7.services.orders.model.Subject;
import org.socraticgrid.hl7.services.orders.model.operationresults.CancelOrderResult;
import org.socraticgrid.hl7.services.orders.model.operationresults.ChangeOrderResult;
import org.socraticgrid.hl7.services.orders.model.operationresults.CreateOrderResult;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.status.CancellationStatus;
import org.socraticgrid.hl7.services.orders.model.status.CreateStatus;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jerry Goodnough
 * 
 */


public class OrderManagementImpl implements OrderManagementIFace {
	private final Logger logger = LoggerFactory.getLogger(OrderManagementImpl.class);

	@Autowired
	MessageIdGenerator idGen;
	@Autowired

	OrderValidator orderValidator;
	
	@Autowired
	OrderManagerIFace orderManager;

	@Autowired
	EventLogger evtLogger;
	
	@Autowired
	OrderDispatcher dispatcher;

	@Override
	public CancelOrderResult cancelOrderById(Identifier orderId)
			throws OrderingException {
		CancelOrderResult result =  dispatcher.cancelOrder(orderId);
		return result;
	}

	@Override
	public <T extends Order> CancelOrderResult cancelOrder(OrderModel<T> order)
			throws OrderingException {
		CancelOrderResult result = new CancelOrderResult();
		result.setStatus(CancellationStatus.NotAllowed);
		return result;
	}

	@Override
	public <T extends Order> ChangeOrderResult changeOrder(Identifier orderId,
			OrderModel<T> order) throws OrderingException {
		logger.debug("In Change Order");
		evtLogger.logSummaryEvent(LogEntryType.User_ChangeOrder, EventLevel.info,
				"", "ChangeOrder", "Validating Message", order);
		try {
			orderValidator.validateMessage(order);

		} catch (OrderingException exp) {
			evtLogger.logUserExceptionEvent("sendMessage", exp);
			throw exp;

		} 
		ChangeOrderResult result =  dispatcher.changeOrder(orderId,order);
		return result;
	}

	@Override
	public <T extends Order> CreateOrderResult createOrder(OrderModel<T> order)
			throws OrderingException {
		logger.debug("In Create Order");
		CreateOrderResult createResult = new CreateOrderResult();
		
		evtLogger.logSummaryEvent(LogEntryType.User_CreateOrder, EventLevel.info,
				"", "CreateOrder", "Validating Message", order);
		try {
			orderValidator.validateMessage(order);

		} catch (OrderingException exp) {
			evtLogger.logUserExceptionEvent("sendMessage", exp);
			throw exp;

		} 
		// Assign the Order an Id
		// TODO  - Deal with assigned Order ids
		Identifier orderId = new Identifier();
		orderId.setValue(idGen.getNewMessageId());
		createResult.setOrderIdentity(orderId);
		evtLogger.logSummaryEvent(LogEntryType.User_CreateOrder, EventLevel.info,
				"", "Create Order", "Order Id Generated = " + orderId.getValue());

		order.getType().setOrderIdentity(orderId);
		

		// Create tracking structures

		
		evtLogger.logSummaryEvent(LogEntryType.User_CreateOrder,
				EventLevel.info, "", "Accepted",
				"Saving Order prior to workflow handoff");

		//Save the Order
		orderManager.saveOrder(order);
		
		//TODO Hand off to Order workflow
		
		CreateStatus ok = dispatcher.dispatchOrder(order);
		
		createResult.setStatus(ok);
		
		return createResult;
	}

	@Override
	public List<OrderSummary> queryOrders(Subject subject) {
		return orderManager.queryOrders(subject);
	}

	@Override
	public OrderModel<? extends Order> retrieveOrder(Identifier orderId) {
		
		return orderManager.retrieveOrder(orderId);
	}

	@Override
	public List<Result> retrieveResults(Identifier resultId) throws OrderingException {
		List<Result> result = dispatcher.retrieveResults(resultId);		
		return result;
	}
	@Override
	public List<ResultAgumentation> getResultAgumentatons(Identifier resultId)
	{
		List<ResultAgumentation> out = new LinkedList<ResultAgumentation>();
		return out;
	}
	
	

}
