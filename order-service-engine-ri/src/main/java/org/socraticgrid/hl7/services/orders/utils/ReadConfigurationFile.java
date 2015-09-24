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
package org.socraticgrid.hl7.services.orders.utils;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadConfigurationFile {
	private static Logger	LOGGER	= LoggerFactory.getLogger(ReadConfigurationFile.class);

	// Compliant
	private ReadConfigurationFile() {
		// Empty private constructor to prevent class instantiation.
	}

	public static Properties getProperties(String resourceName) {
		InputStream stream = ReadConfigurationFile.class.getClassLoader().getResourceAsStream(
				resourceName);
		Properties p = new Properties();
		try {
			p.load(stream);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return p;
	}

	public static InputStream getResourceInputStream(String resourceName) {
		InputStream stream = ReadConfigurationFile.class.getClassLoader().getResourceAsStream(
				resourceName);
		return stream;
	}
}