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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.exceptions.NotAuthorizedException;
import org.socraticgrid.hl7.services.orders.interfaces.OrderServiceMonitoringIFace;
import org.socraticgrid.hl7.services.orders.internal.interfaces.OrderManagerIFace;
import org.socraticgrid.hl7.services.orders.model.FulfillmentInfo;
import org.socraticgrid.hl7.services.orders.model.FulfillmentStatistics;
import org.socraticgrid.hl7.services.orders.model.OSServerStatus;
import org.socraticgrid.hl7.services.orders.model.OSServiceStatistics;
import org.socraticgrid.hl7.services.orders.model.ServerState;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderServiceMonitoringImpl implements OrderServiceMonitoringIFace{

	private final Logger logger = LoggerFactory.getLogger(OrderServiceMonitoringImpl.class);
	 
	@Autowired
	OrderManagerIFace iface;
	
	@Override
	public OSServerStatus getServerStatus() throws NotAuthorizedException {
		OSServerStatus status = new OSServerStatus(); 
		try{
			status.setName("Server Status");
			status.setState(ServerState.Running);
			int size = iface.getOrderSize();
		}catch(Exception e){
			status.setState(ServerState.Shutdown);
		} 
		return status;
	}

	@Override
	public FulfillmentStatistics getFulfillmentStatistics(String itemType)
			throws NotAuthorizedException {
		FulfillmentStatistics stat = new FulfillmentStatistics();
		int size= iface.getOrderSize();
		stat.setNoOfOrders(size);
		return stat;
	}

	@Override
	public OSServiceStatistics getServiceStatistics()
			throws NotAuthorizedException {
		OSServiceStatistics stat = new OSServiceStatistics();
		
		//TODO replace with actual implementation
		stat.setAvgRespLatency(1);
		stat.setNoOfTransactions(1);
		stat.setOperationPeriod(1);
		return stat;
	}

	@Override
	public FulfillmentInfo getFulfillmentInformation()
			throws NotAuthorizedException {
		FulfillmentInfo info = new FulfillmentInfo();
		
		List<String> fullfillmentTypes = new ArrayList<>();
		fullfillmentTypes.add("lab");
		fullfillmentTypes.add("medication");
		fullfillmentTypes.add("nursing");
		fullfillmentTypes.add("nutrition");
		
		info.setFullfillmentTypes(fullfillmentTypes);
		return info;
	}
}
