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
import org.socraticgrid.hl7.services.orders.dao.OrderCatalogDao;
import org.socraticgrid.hl7.services.orders.interfaces.OrderCatalogQueryIFace;
import org.socraticgrid.hl7.services.orders.model.CatalogItem;
import org.socraticgrid.hl7.services.orders.model.CatalogItemTree;
import org.socraticgrid.hl7.services.orders.model.CatalogItemType;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderCatalogQueryImpl implements OrderCatalogQueryIFace {

	private final Logger logger = LoggerFactory
			.getLogger(OrderCatalogQueryImpl.class);

	@Autowired
	private OrderCatalogDao orderCatalogDao;

	@Override
	public CatalogItem getItemDetail(Identifier itemId) {
		CatalogItem catalogItem = null;
		try {
			catalogItem = orderCatalogDao.getItemDetail(itemId);
			logger.debug("CatalogItem : " + catalogItem);
		} catch (Exception e) {
			logger.error(
					"Some error occurred while fetching Item Detail from database! Reason : "
							+ e.getMessage(), e);
		}
		return catalogItem;
	}

	@Override
	public <T extends Order> OrderModel<T> getItemPrototype(Identifier itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CatalogItemTree queryItemTree(Identifier itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CatalogItemType> queryItemTypes(String query) {
		List<CatalogItemType> result = new ArrayList<CatalogItemType>();
		try {
			result = orderCatalogDao.getAllCatalogItemTypes();
			logger.debug("CatalogItemTypes : " + result);
		} catch (Exception e) {
			logger.error(
					"Some error occurred while fetching Catalog Item Types from database! Reason : "
							+ e.getMessage(), e);
		}
		return result;
	}

}
