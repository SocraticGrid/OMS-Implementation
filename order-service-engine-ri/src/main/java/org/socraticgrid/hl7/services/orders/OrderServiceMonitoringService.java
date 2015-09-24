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

import javax.jws.WebParam;
import javax.jws.WebService;

import org.socraticgrid.hl7.services.orders.exceptions.NotAuthorizedException;
import org.socraticgrid.hl7.services.orders.interfaces.OrderServiceMonitoringIFace;
import org.socraticgrid.hl7.services.orders.model.FulfillmentInfo;
import org.socraticgrid.hl7.services.orders.model.FulfillmentStatistics;
import org.socraticgrid.hl7.services.orders.model.OSServerStatus;
import org.socraticgrid.hl7.services.orders.model.OSServiceStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@WebService(name = "ordersystemmonitoring", targetNamespace = "org.socraticgrid.hl7.services.orders")
public class OrderServiceMonitoringService implements OrderServiceMonitoringIFace { 

	@Autowired
	@Qualifier("OrderSystemMonitoringServiceImpl")
	OrderServiceMonitoringIFace service; 

	@Override
	public OSServerStatus getServerStatus() throws NotAuthorizedException { 
		return service.getServerStatus();
	}

	@Override
	public FulfillmentStatistics getFulfillmentStatistics(@WebParam(name = "itemType") String itemType)
			throws NotAuthorizedException {
		 
		return service.getFulfillmentStatistics(itemType);
	}

	@Override
	public OSServiceStatistics getServiceStatistics()
			throws NotAuthorizedException {
		return service.getServiceStatistics();
	}

	@Override
	public FulfillmentInfo getFulfillmentInformation()
			throws NotAuthorizedException { 
		return service.getFulfillmentInformation();
	} 

}
