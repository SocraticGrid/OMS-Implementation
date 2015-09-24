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
package org.socraticgrid.hl7.services.orders.clients.orderfulfillment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.exceptions.OrderingException;
import org.socraticgrid.hl7.services.orders.interfaces.FulfillmentIFace;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.Promise;
import org.socraticgrid.hl7.services.orders.model.Result;
import org.socraticgrid.hl7.services.orders.model.ResultAgumentation;
import org.socraticgrid.hl7.services.orders.model.primatives.Code;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.requirements.EndorsementRequirement;
import org.socraticgrid.hl7.services.orders.model.requirements.Requirement;
import org.socraticgrid.hl7.services.orders.model.status.CancellationStatus;
import org.socraticgrid.hl7.services.orders.model.status.VerifyStatus;
import org.socraticgrid.hl7.services.orders.model.types.orderitems.MedicationOrderItem;

public class FulfillmentImpl implements FulfillmentIFace {
	private final Logger logger = LoggerFactory.getLogger(FulfillmentImpl.class);
	
	HashMap<String,OrderTrack> orders = new HashMap<String,OrderTrack>();
	HashMap<String,List<Result>> results = new HashMap<String,List<Result>>();
	 
	@Override
	public CancellationStatus cancelOrder(Identifier orderId){ 
		OrderTrack track = orders.get(orderId.getValue());
		Order order;
		if (track == null) {
			return CancellationStatus.NotAllowed;
		}
		if (track != null){
			order = track.order;
			if(order!=null && order.getStatus()!=null){
				if("Completed".equalsIgnoreCase(order.getStatus().getCode())){ 
					logger.debug("Order "+orderId+" not cancelled as the status of order is completed.");
					return CancellationStatus.AlreadyComplete;
				}
				order.getStatus().setCode("Cancelled");
				track.order = order;
				orders.put(orderId.getValue(), track);
			}
		}
		 
		logger.debug("Order "+orderId+" cancelled.");
		return CancellationStatus.Sucessful;
		
	}

	@Override
	public <T extends Order> OrderModel<T> getProposedOrderReplacement(
			Identifier orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResultAgumentation> getResultAgumentatons(Identifier resultId) {
		// TODO Auto-generated method stub
		List<ResultAgumentation> out = new LinkedList<ResultAgumentation>();
		return out;
	}

	@Override
	public <T extends Order> Promise requestFulfillment(OrderModel<T> order) {
		logger.debug("Requesting fulfillemt of Order "+order.toString());
		Identifier id = new Identifier();
		id.setValue("FulId");
		Promise out = new Promise();
		out.setFulfillmentIdentity(id);
		Identifier orderId = order.getType().getOrderIdentity();
		out.setOrderIdentity(orderId);
		MedicationOrderItem med1 = new MedicationOrderItem();
		med1.setComment("Medication 1");
		out.getPromisedItems().add(med1);
		MedicationOrderItem med2 = new MedicationOrderItem();
		med1.setComment("Medication 2");
		out.getPromisedItems().add(med2);
		EndorsementRequirement req = new EndorsementRequirement();
		req.setSeed("Nounce");
		req.setScheme("test");
		Properties props = new Properties();
		props.setProperty("SomeKey", "SomeValue");
		req.setProperties(props);
		out.getRequirements().add(req);
		Code cd  = new Code();
		cd.setCode("PendingRequirements");
		cd.setCodeSystem("OrderService");
		out.setStatus(cd);
		logger.debug("Fulfillemt of Order "+order.toString());
		
		OrderTrack track = new OrderTrack(order.getType(),out);
		orders.put(orderId.getValue(), track);
		return out;
	}

	@Override
	public List<Result> retrieveResultByOrderId(Identifier orderId) {
		logger.debug("Fetch Results for order Id:"+orderId); 
		return results.get(orderId.getValue());
	}

	@Override
	public Result retrieveResultByResultId(Identifier resultId) { 
		Result out = null;
		logger.debug("Retrieve result by Result Id: "+resultId);
		return out;
	}
	
	public Promise updateOrderRequirements(Identifier orderId, List<Requirement> requirements) {
		Promise out = null;
		
		
	
		OrderTrack track = orders.get(orderId.getValue());
		if (track != null)
		{
			out = track.promise;
			
			Iterator<Requirement> itr = requirements.iterator();
			while(itr.hasNext())
			{
				Requirement requirement = itr.next();
				
				if (requirement instanceof EndorsementRequirement)
				{
					EndorsementRequirement ereq = (EndorsementRequirement) requirement;
					if (ereq.getUserId() != null)
					{
						if (ereq.getSignature() != null)
						{
							//TODO  Add scheme based checking
							Code cd  = new Code();
							cd.setCode("InFulfillmentProcess");
							cd.setCodeSystem("OrderService");
							out.setStatus(cd);
							itr.remove();
						}
					}
				}
			}
		}
		
		return out;
	}

	@Override
	public VerifyStatus verifyOrderRequirement(Identifier orderId,
			Requirement requirement) throws OrderingException {
		VerifyStatus out = VerifyStatus.Invalid;
		OrderTrack track = orders.get(orderId.getValue());
		if (track != null)
		{
			if (requirement instanceof EndorsementRequirement)
			{
				EndorsementRequirement ereq = (EndorsementRequirement) requirement;
				if (ereq.getUserId() != null)
				{
					if (ereq.getSignature() != null)
					{
						//To do - Check the signature
						out = VerifyStatus.Valid;
					}
				}
			}
		}
		return out;
	}

	class OrderTrack
	{
		public OrderTrack(Order order, Promise promise)
		{
			this.order=order;
			this.promise=promise;
		}
		
		public Order order;
		public Promise promise;
	}
}
