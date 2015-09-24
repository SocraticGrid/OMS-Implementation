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
import org.socraticgrid.hl7.services.orders.model.entity.ClinicalPractitioner;
import org.socraticgrid.hl7.services.orders.model.entity.Identifier;
import org.springframework.stereotype.Repository;

@Repository
public class ClinicalPractitionerDaoImpl extends
		GenericDaoImpl<ClinicalPractitioner, Long> implements
		ClinicalPractitionerDao {

	private static Logger LOGGER = LoggerFactory
			.getLogger(ClinicalPractitionerDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public ClinicalPractitioner getByIdentity(Identifier identifier) {
		ClinicalPractitioner clinicalPractitioner = null;
		LOGGER.debug(">> getByIdentity");
		Criteria criteria = getCriteria().createAlias("identity", "cp_id").add(
				Restrictions.eq("cp_id.value", identifier.getValue()));
		List<ClinicalPractitioner> clinicalPractitioners = criteria.list();
		if (clinicalPractitioners != null && clinicalPractitioners.size() > 0) {
			clinicalPractitioner = clinicalPractitioners.get(0);
		}
		LOGGER.debug("getByIdentity <<");
		return clinicalPractitioner;
	}
}
