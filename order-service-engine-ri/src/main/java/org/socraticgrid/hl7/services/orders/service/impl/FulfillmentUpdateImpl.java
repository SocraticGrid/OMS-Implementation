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

import java.util.List;

import org.socraticgrid.hl7.services.orders.interfaces.FulfillmentUpdateIFace;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.Result;
import org.socraticgrid.hl7.services.orders.model.ResultAgumentation;
import org.socraticgrid.hl7.services.orders.model.primatives.Code;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.status.UpdateStatus;

public class FulfillmentUpdateImpl implements FulfillmentUpdateIFace{

	@Override
	public <T extends Order> boolean proposeOrderReplacement(Identifier orderId,
			OrderModel<T> order) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean submitResultAgmentation(Identifier resultId,
			List<ResultAgumentation> agumentation) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public UpdateStatus updateOrderStatus(Identifier orderId, Code status) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public UpdateStatus updateResult(Identifier resultId, Result result) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public UpdateStatus updateResultStatus(Identifier resultId, Code status) {
		// TODO Auto-generated method stub
		return null;
	}

}
