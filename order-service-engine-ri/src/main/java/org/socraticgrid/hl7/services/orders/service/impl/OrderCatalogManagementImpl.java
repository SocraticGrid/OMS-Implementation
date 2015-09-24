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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.dao.OrderCatalogDao;
import org.socraticgrid.hl7.services.orders.interfaces.OrderCatalogManagementIFace;
import org.socraticgrid.hl7.services.orders.model.CatalogItem;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.socraticgrid.hl7.services.orders.model.status.UpdateStatus;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderCatalogManagementImpl implements OrderCatalogManagementIFace {

	private final Logger logger = LoggerFactory
			.getLogger(OrderCatalogManagementImpl.class);

	@Autowired
	private OrderCatalogDao orderCatalogDao;

	@Override
	public CatalogItem getCatalogItem(Identifier itemId) {
		CatalogItem catalogItem = null;
		try {
			catalogItem = orderCatalogDao.getCatalogItem(itemId);
			logger.debug("CatalogItem : " + catalogItem);
		} catch (Exception e) {
			logger.error(
					"Some error occurred while fetching Catalog Item from database! Reason : "
							+ e.getMessage(), e);
		}
		return catalogItem;
	}

	@Override
	public UpdateStatus updateCatalogItem(CatalogItem catalogItem) {
		UpdateStatus status = UpdateStatus.Sucessful;
		try {
			status = orderCatalogDao.updateCatalogItem(catalogItem);
		} catch (Exception e) {
			status = UpdateStatus.Failed;
			logger.error(
					"Some error occurred while updating Catalog Item into database! Reason : "
							+ e.getMessage(), e);
		}
		return status;
	}

	@Override
	public UpdateStatus deleteCatalogItem(Identifier itemId) {
		UpdateStatus status = UpdateStatus.Sucessful;
		try {
			status = orderCatalogDao.deleteCatalogItem(itemId);
		} catch (Exception e) {
			status = UpdateStatus.Failed;
			logger.error(
					"Some error occurred while updating Catalog Item into database! Reason : "
							+ e.getMessage(), e);
		}
		return status;
	}

	@Override
	public UpdateStatus createCatalogItem(CatalogItem catalogItem) {
		UpdateStatus status = UpdateStatus.Sucessful;
		try {
			status = orderCatalogDao.createCatalogItem(catalogItem);
		} catch (Exception e) {
			status = UpdateStatus.Failed;
			logger.error(
					"Some error occurred while creating Catalog Item into database! Reason : "
							+ e.getMessage(), e);
		}
		return status;
	}

}
