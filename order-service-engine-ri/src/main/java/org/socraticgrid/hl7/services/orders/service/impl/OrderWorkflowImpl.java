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

import org.socraticgrid.hl7.services.orders.exceptions.OrderingException;
import org.socraticgrid.hl7.services.orders.functional.EventLogger;
import org.socraticgrid.hl7.services.orders.functional.OrderDispatcher;
import org.socraticgrid.hl7.services.orders.functional.OrderValidator;
import org.socraticgrid.hl7.services.orders.interfaces.OrderWorkflowIFace;
import org.socraticgrid.hl7.services.orders.internal.interfaces.OrderManagerIFace;
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

public class OrderWorkflowImpl implements OrderWorkflowIFace {
	

	OrderValidator orderValidator;
	
	@Autowired
	OrderManagerIFace orderManager;

	@Autowired
	EventLogger evtLogger;
	
	@Autowired
	OrderDispatcher dispatcher;


	@Override
	public <T extends Order> ChangeStatus changeOrder(Identifier orderId,
			OrderModel<T> order) throws OrderingException {
	
		return ChangeStatus.Failed;
	}

	@Override
	public <T extends Workflow> WorkflowModel<T> getOrderWorkflow(
			Identifier orderId) {
	
		return null;
	}

	@Override
	public List<Requirement> queryOrderRequirements(Identifier orderId) {
		List<Requirement> result = new LinkedList<Requirement>();
		return result;
	}

	@Override
	public List<OrderSummary> queryOrders(Subject subject) {
		List<OrderSummary> result = new LinkedList<OrderSummary>();
		return result;
	}

	@Override
	public <T extends Order> OrderModel<T> retrieveOrder(Identifier orderId) {
	
		return null;
	}


	@Override
	public UpdateStatus updateOrderStatus(Identifier orderId, Code status)
			throws OrderingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Workflow> UpdateStatus updateOrderWorkflow(
			Identifier orderId, WorkflowModel<T> updatedWorkflow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpdateStatus updateOrderRequirements(Identifier orderId, List<Requirement> requirements)  
			throws OrderingException {
			return dispatcher.updateOrderRequirements(orderId, requirements);
	}

	@Override
	public VerifyStatus verifyOrderRequirement(Identifier orderId, Requirement requirement)  
			throws OrderingException {
		return dispatcher.verifyOrderRequirement(orderId, requirement);
		
	}
}
