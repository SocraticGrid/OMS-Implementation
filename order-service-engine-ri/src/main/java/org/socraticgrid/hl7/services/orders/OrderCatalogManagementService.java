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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.interfaces.OrderCatalogManagementIFace;
import org.socraticgrid.hl7.services.orders.model.CatalogItem;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.status.UpdateStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@WebService(name = "ordercatalogmanagement", targetNamespace = "org.socraticgrid.hl7.services.orders")
public class OrderCatalogManagementService implements OrderCatalogManagementIFace {

	private final Logger logger = LoggerFactory
			.getLogger(OrderCatalogManagementService.class);

	@Autowired
	@Qualifier("OrderCatalogManagementServiceImpl")
	OrderCatalogManagementIFace service;

	@Override
	public CatalogItem getCatalogItem(@WebParam(name = "item") Identifier item) {
		return service.getCatalogItem(item);
	}

	@Override
	public UpdateStatus updateCatalogItem(
			@WebParam(name = "catalogItem") CatalogItem catalogItem) {
		return service.updateCatalogItem(catalogItem);
	}
	
	@Override
	public UpdateStatus deleteCatalogItem(
			@WebParam(name = "item") Identifier item) {
		return service.deleteCatalogItem(item);
	}

	@Override
	public UpdateStatus createCatalogItem(
			@WebParam(name = "catalogItem") CatalogItem catalogItem) {
		return service.createCatalogItem(catalogItem);
	}

}
