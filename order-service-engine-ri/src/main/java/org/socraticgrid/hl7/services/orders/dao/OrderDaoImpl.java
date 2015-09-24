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
package org.socraticgrid.hl7.services.orders.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.model.Patient;
import org.socraticgrid.hl7.services.orders.model.entity.Order;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDaoImpl extends GenericDaoImpl<Order, Long> implements
		OrderDao {

	private static Logger LOGGER = LoggerFactory.getLogger(OrderDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getIdentityValuesByPatient(Patient patient) {
		LOGGER.debug(">> getIdentityValuesByPatient");
		List<String> values = new ArrayList<String>();
		Criteria criteria = getCriteria()
				.createAlias("orderIdentity", "id")
				.createAlias("subject", "patient")
				.createAlias("patient.identity", "pId")
				.createAlias("patient.gender", "pGender")
				.add(Restrictions.eq("name", patient.getName()))
				.add(Restrictions.eq("dateOfBirth", patient.getDateOfBirth()))
				.add(Restrictions.eq("pGender.code", patient.getGender()
						.getCode()))
				.add(Restrictions.eq("pGender.codeSystem", patient.getGender()
						.getCodeSystem()))
				.add(Restrictions.eq("pGender.text", patient.getGender()
						.getText()))
				.add(Restrictions.eq("pGender.label", patient.getGender()
						.getLabel()))
				.add(Restrictions.eq("pId.value", patient.getIdentity()
						.getValue()))
				.setProjection(Projections.property("id.value"));
		values = criteria.list();
		LOGGER.debug("getIdentityValuesByPatient <<");
		return values;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Order getOrderByIdentity(Identifier identifier) {
		LOGGER.debug(">> getIdentityValuesByPatient");
		Order order = null;
		Criteria criteria = getCriteria().createAlias("orderIdentity", "id")
				.add(Restrictions.eq("id.value", identifier.getValue()))
				.addOrder(org.hibernate.criterion.Order.desc("orderTime"));
		List<Order> orders = criteria.list();
		if (orders != null && orders.size() > 0) {
			order = orders.get(0);
		}
		LOGGER.debug("getIdentityValuesByPatient <<");
		return order;
	}
}
