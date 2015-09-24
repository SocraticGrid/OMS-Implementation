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
package org.socraticgrid.hl7.services.orders.internal.interfaces;

import java.util.List;

import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.OrderSummary;
import org.socraticgrid.hl7.services.orders.model.Promise;
import org.socraticgrid.hl7.services.orders.model.Subject;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;

/**
 * @author steven
 *
 */
public interface OrderManagerIFace {

	public abstract <T extends Order> void saveOrder(OrderModel<T> order);
	
	public abstract OrderModel<? extends Order> retrieveOrder(Identifier identifier);
	
	public abstract int getOrderSize();
	
	public abstract List<OrderSummary> queryOrders(Subject subject);
	
	public abstract <T extends Order> void updateOrder(OrderModel<T> order);
	
	public abstract void updateOrderPromise(Identifier identifier,Promise promise);
	
	public abstract void saveOrderPromise(Identifier identifier,Promise promise);
	
	public abstract Promise retrievePromise(Identifier identifier);
}
