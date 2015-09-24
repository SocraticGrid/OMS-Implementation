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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.socraticgrid.hl7.services.orders.exceptions.OrderingException;
import org.socraticgrid.hl7.services.orders.internal.interfaces.ValidatorStepIFace;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;

/**
 * The MessageValidator  is used to validate all types of messages - First a 
 * common pass is made and then message specific checks are made - Faild validation will result
 * an exception being thrown.
 * 
 * This class is configured 
 * @author Jerry Goodnough
 * @version 1.0
 * @created 16-Dec-2013 3:54:57 PM
 */
public class OrderValidator
{

	public OrderValidator()
	{

	}


	private List<ValidatorStepIFace> commonValidationSteps;

	public List<ValidatorStepIFace> getCommonValidationSteps()
	{
		return commonValidationSteps;
	}

	public void setCommonValidationSteps(
			List<ValidatorStepIFace> commonValidationSteps)
	{
		this.commonValidationSteps = commonValidationSteps;
	}

	private Map<String, List<ValidatorStepIFace>> typeSpecificValidators;

	public Map<String, List<ValidatorStepIFace>> getTypeSpecificValidators()
	{
		return typeSpecificValidators;
	}

	public void setTypeSpecificValidators(
			Map<String, List<ValidatorStepIFace>> typeSpecificValidators)
	{
		this.typeSpecificValidators = typeSpecificValidators;
	}

	public <T extends Order> boolean validateMessage(
			OrderModel<T> order) throws OrderingException
	{
		// First check the common validation steps
		if (commonValidationSteps != null)
		{
			Iterator<ValidatorStepIFace> itr = commonValidationSteps.iterator();
			while (itr.hasNext())
			{
				itr.next().validateOrder(order);
			}
		}

		// Now Lookup the chain for the specific type
		String typeName = order.getType().getClass().getSimpleName();
		List<ValidatorStepIFace> chkList = typeSpecificValidators.get(typeName);
		if (chkList != null)
		{
			Iterator<ValidatorStepIFace> itr = chkList.iterator();
			while (itr.hasNext())
			{
				itr.next().validateOrder(order);
			}
		}

		return true;
	}
}