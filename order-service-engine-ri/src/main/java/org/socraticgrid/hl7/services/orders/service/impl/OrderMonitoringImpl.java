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

import org.socraticgrid.hl7.services.orders.interfaces.OrderMonitoringIFace;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.OrderSummary;
import org.socraticgrid.hl7.services.orders.model.Workflow;
import org.socraticgrid.hl7.services.orders.model.WorkflowModel;
import org.socraticgrid.hl7.services.orders.model.primatives.Code;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;

public class OrderMonitoringImpl implements OrderMonitoringIFace{

	@Override
	public List<OrderSummary> queryPendingOrders(String context) {
		List<OrderSummary> result = new LinkedList<OrderSummary>();
		return result;
	}

	@Override
	public <T extends Order> OrderModel<T> retrieveOrder(Identifier orderId) {
	
		return null;
	}

	@Override
	public Code retrieveStatus(Identifier orderId) {

		return null;
	}

	@Override
	public <T extends Workflow> WorkflowModel<T> retriveOrderWorkflow(
			Identifier orderId) {

		return null;
	}

	
}
