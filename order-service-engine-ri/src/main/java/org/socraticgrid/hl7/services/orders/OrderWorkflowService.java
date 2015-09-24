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
import org.socraticgrid.hl7.services.orders.interfaces.OrderWorkflowIFace;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.OrderSummary;
import org.socraticgrid.hl7.services.orders.model.Subject;
import org.socraticgrid.hl7.services.orders.model.Workflow;
import org.socraticgrid.hl7.services.orders.model.WorkflowModel;
import org.socraticgrid.hl7.services.orders.model.primatives.Code;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.requirements.Requirement;
import org.socraticgrid.hl7.services.orders.model.status.ChangeStatus;
import org.socraticgrid.hl7.services.orders.model.status.UpdateStatus;
import org.socraticgrid.hl7.services.orders.model.status.VerifyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
@WebService(name = "orderworkflow", targetNamespace = "org.socraticgrid.hl7.services.orders")
public class OrderWorkflowService implements OrderWorkflowIFace {
	private final Logger logger = LoggerFactory
			.getLogger(OrderWorkflowService.class);

	@Autowired
	@Qualifier("OrderWorkflowServiceImpl")
	OrderWorkflowIFace service;


	public <T extends Order> ChangeStatus changeOrder(@WebParam(name = "orderId")Identifier orderId,
			@WebParam(name = "updatedOrder")OrderModel<T> updatedOrder) throws OrderingException {
		return service.changeOrder(orderId, updatedOrder);
	}


	public <T extends Workflow> WorkflowModel<T> getOrderWorkflow(
			@WebParam(name = "orderId")Identifier orderId) {
		return service.getOrderWorkflow(orderId);
	}


	public List<Requirement> queryOrderRequirements(@WebParam(name = "orderId")Identifier orderId) {
		return service.queryOrderRequirements(orderId);
	}


	public List<OrderSummary> queryOrders(@WebParam(name = "orderId")Subject subject) {
		return service.queryOrders(subject);
	}


	public <T extends Order> OrderModel<T> retrieveOrder(@WebParam(name = "orderId")Identifier orderId) {
		return service.retrieveOrder(orderId);
	}


	public UpdateStatus updateOrderRequirements(@WebParam(name = "orderId")Identifier orderId,
			@WebParam(name = "updatedRequirements")List<Requirement> updatedRequirements) throws OrderingException {
		return service.updateOrderRequirements(orderId, updatedRequirements);
	}


	public UpdateStatus updateOrderStatus(@WebParam(name = "orderId")Identifier orderId, Code status)
			throws OrderingException {
		return service.updateOrderStatus(orderId, status);
	}


	public <T extends Workflow> UpdateStatus updateOrderWorkflow(
			@WebParam(name = "orderId")Identifier orderId,@WebParam(name = "workflowModel") WorkflowModel<T> workflowModel) {
		return service.updateOrderWorkflow(orderId, workflowModel);
	}


	@Override
	public VerifyStatus verifyOrderRequirement(Identifier orderId,
			Requirement requirement) throws OrderingException {
		return service.verifyOrderRequirement(orderId, requirement);
	}

}
