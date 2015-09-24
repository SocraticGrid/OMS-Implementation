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
import org.socraticgrid.hl7.services.orders.exceptions.OrderingException;
import org.socraticgrid.hl7.services.orders.interfaces.OrderManagementIFace;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

/**
 * Provides a SOAP front end for the client service
 * 
 * @author Steven Elliott and Jerry Goodnough
 * 
 */

@WebService(name = "ordermanagement", targetNamespace = "org.socraticgrid.hl7.services.orders")
public class OrderManagementService implements OrderManagementIFace {

	private final Logger logger = LoggerFactory
			.getLogger(OrderManagementService.class);

	@Autowired
	@Qualifier("OrderManagementServiceImpl")
	OrderManagementIFace orderService;
	
	@Autowired
	ApplicationContext subjectCtx;

	/*
	 * public boolean assertPresence(@WebParam(name = "userId") String userId,
	 * 
	 * @WebParam(name = "context") String context,
	 * 
	 * @WebParam(name = "status") String status) throws
	 * FeatureNotSupportedException, UnknownUserException { return
	 * clientService.assertPresence(userId, context, status); }
	 */

	public CancelOrderResult cancelOrderById(
			@WebParam(name = "orderId") Identifier orderId)
			throws OrderingException {
		logger.debug("In createOrder Web Service");
		CancelOrderResult out = orderService.cancelOrderById(orderId);
		logger.debug("Leaving createOrder Web Service");
		return out;
	}

	public <T extends Order> CancelOrderResult cancelOrder(
			@WebParam(name = "order") OrderModel<T> order)
			throws OrderingException {
		logger.debug("In cancelOrder Web Service");
		CancelOrderResult out = orderService.cancelOrder(order);
		logger.debug("Leaving cancelOrder Web Service");
		return out;
	}

	public <T extends Order> ChangeOrderResult changeOrder(
			@WebParam(name = "orderId") Identifier orderId,
			@WebParam(name = "order") OrderModel<T> order)
			throws OrderingException {
		logger.debug("In changeOrder Web Service");
		ChangeOrderResult out = orderService.changeOrder(orderId, order);
		logger.debug("Leaving changeOrder Web Service");
		return out;
	}

	public <T extends Order> CreateOrderResult createOrder(
			@WebParam(name = "order") OrderModel<T> order)
			throws OrderingException {
		logger.debug("In createOrder Web Service");
		CreateOrderResult out =  orderService.createOrder(order);
		logger.debug("Leaving createOrder Web Service");
		return out;
		
	}

	public List<OrderSummary> queryOrders(
			@WebParam(name = "subjectIndentifer") Subject subject) {
		logger.debug("In queryOrders Web Service");
		 List<OrderSummary>  out =  orderService.queryOrders(subject);
		logger.debug("Leaving queryOrders Web Service");
		return out;
	}

	public OrderModel<? extends Order> retrieveOrder(
			@WebParam(name = "orderId") Identifier orderId) {
		logger.debug("In retrieveOrder Web Service");
		OrderModel<? extends Order> out = orderService.retrieveOrder(orderId);
		logger.debug("Leaving retrieveOrder Web Service");
		return out;
	}

	public List<Result> retrieveResults(
			@WebParam(name = "resultIndentifer") Identifier resultId) throws OrderingException {
		logger.debug("In retrieveResults Web Service");
		List<Result>  out = orderService.retrieveResults(resultId);
		logger.debug("Leaving retrieveResults Web Service");
		return out;
	}
	
	public List<ResultAgumentation> getResultAgumentatons(	@WebParam(name = "resultIndentifer")Identifier resultId)
	{
		logger.debug("In getResultAgumentatons Web Service");
		List<ResultAgumentation> out =  orderService.getResultAgumentatons(resultId);
		logger.debug("Leaving getResultAgumentatons Web Service");
		return out;
	}
}
