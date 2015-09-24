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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.model.entity.Promise;
import org.socraticgrid.hl7.services.orders.model.primatives.Identifier;
import org.springframework.stereotype.Repository;

@Repository
public class PromiseDaoImpl extends GenericDaoImpl<Promise, Long> implements
		PromiseDao {

	private static Logger LOGGER = LoggerFactory
			.getLogger(PromiseDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public Promise getByIdentifier(Identifier identifier) {
		LOGGER.debug(">> getByIdentifier");
		Promise promise = null;
		Criteria criteria = getCriteria().createAlias("promiseIdentity", "id")
				.add(Restrictions.eq("id.value", identifier.getValue()));
		List<Promise> promises = criteria.list();
		if (promises != null && promises.size() > 0) {
			promise = promises.get(0);
		}
		LOGGER.debug("getByIdentifier <<");
		return promise;
	}

}
