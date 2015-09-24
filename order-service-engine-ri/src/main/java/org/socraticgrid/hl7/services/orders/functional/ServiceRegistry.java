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
package org.socraticgrid.hl7.services.orders.functional;
import java.util.HashMap;
import java.util.Set;

import org.socraticgrid.hl7.services.orders.interfaces.FulfillmentIFace;

/**
 * @author Jerry Goodnough
 * @version 1.0
 * @created 16-Dec-2013 3:54:57 PM
 */
public class ServiceRegistry {


	private HashMap<String,FulfillmentIFace> serviceMap = new HashMap<String,FulfillmentIFace>();

	public ServiceRegistry(){

	}

	/**
	 * @return the serviceMap
	 */
	public HashMap<String, FulfillmentIFace> getServiceMap() {
		return serviceMap;
	}

	/**
	 * @param serviceMap the serviceMap to set
	 */
	public void setServiceMap(HashMap<String, FulfillmentIFace> serviceMap) {
		this.serviceMap = serviceMap;
	}

	public FulfillmentIFace getService(String name)
	{
		return serviceMap.get(name);
	}
	
	public Set<String> getServiceNames()
	{
		return serviceMap.keySet();
	}
}