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
import org.socraticgrid.hl7.services.orders.model.entity.Identifier;
import org.socraticgrid.hl7.services.orders.model.entity.Subject;
import org.springframework.stereotype.Repository;

@Repository
public class SubjectDaoImpl extends GenericDaoImpl<Subject, Long> implements
		SubjectDao {

	private static Logger LOGGER = LoggerFactory
			.getLogger(SubjectDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public Subject getByIdentity(Identifier identifier) {
		Subject subject = null;
		LOGGER.debug(">> getByIdentity");
		Criteria criteria = getCriteria().createAlias("identity", "sub_id")
				.add(Restrictions.eq("sub_id.value", identifier.getValue()));
		List<Subject> subjects = criteria.list();
		if (subjects != null && subjects.size() > 0) {
			subject = subjects.get(0);
		}
		LOGGER.debug("getByIdentity <<");
		return subject;
	}
}
