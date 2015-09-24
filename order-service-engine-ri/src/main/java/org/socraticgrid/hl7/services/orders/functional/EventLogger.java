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
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socraticgrid.hl7.services.orders.logging.DetailedLogEntry;
import org.socraticgrid.hl7.services.orders.logging.EventLevel;
import org.socraticgrid.hl7.services.orders.logging.LogEntry;
import org.socraticgrid.hl7.services.orders.logging.LogEntryGroup;
import org.socraticgrid.hl7.services.orders.logging.LogEntryMappings;
import org.socraticgrid.hl7.services.orders.logging.LogEntryType;
import org.socraticgrid.hl7.services.orders.logging.SummaryLogEntry;
import org.socraticgrid.hl7.services.orders.model.Order;
import org.socraticgrid.hl7.services.orders.model.OrderModel;


public class EventLogger {
	private final Logger logger = LoggerFactory.getLogger(EventLogger.class);

	private static Map<LogEntryGroup, EventLevel> mapActiveEventLevels;

	static {
		mapActiveEventLevels = new HashMap<LogEntryGroup, EventLevel>();
		mapActiveEventLevels.put(LogEntryGroup.Authenication, EventLevel.all);
		mapActiveEventLevels.put(LogEntryGroup.Exception, EventLevel.all);
		mapActiveEventLevels.put(LogEntryGroup.Create, EventLevel.all);
		mapActiveEventLevels.put(LogEntryGroup.Fullfilment, EventLevel.all);
		mapActiveEventLevels.put(LogEntryGroup.Workflow, EventLevel.all);
		mapActiveEventLevels.put(LogEntryGroup.Update, EventLevel.all);
		mapActiveEventLevels.put(LogEntryGroup.View, EventLevel.all);
		mapActiveEventLevels.put(LogEntryGroup.Operational, EventLevel.all);
		mapActiveEventLevels.put(LogEntryGroup.Action, EventLevel.all);
		mapActiveEventLevels.put(LogEntryGroup.Diagnostic, EventLevel.all);
;
	}

	private  EventLevel findGroupLoggingLevel(LogEntryType entry) {
		LogEntryGroup grp = LogEntryMappings.getLogEntryGroup(entry);

		if ((grp != null) && (mapActiveEventLevels.containsKey(grp))) {
			return mapActiveEventLevels.get(grp);
		} else {
			logger.trace("Defaulting to degug event level for "+entry.toString());
			return EventLevel.debug;
		}
	}

	public boolean isLoggable(LogEntryType entry, EventLevel level) {
		EventLevel grpLvl = findGroupLoggingLevel(entry);
		int grpVal = grpLvl.getNumVal();
		int eventVal = level.getNumVal();
	
		logger.trace("Is "+level+"("+eventVal+") >= "+grpLvl+"("+grpVal+")");
		
		boolean willLog = level.getNumVal() >= (findGroupLoggingLevel(entry)
				.getNumVal());
		
		if (willLog == false) {
			logger.trace("Rejecting logging at Level " + level+" for "+entry);
		}
		return willLog;
	}

	private int eventBufferSize = 100;

	private CircularFifoQueue<LogEntry> eventQueue;

	public EventLogger() {

	}

	/**
	 * @return the eventBufferSize
	 */
	public int getEventBufferSize() {
		return eventBufferSize;
	}

	/**
	 * @return the eventQueue
	 */
	public CircularFifoQueue<LogEntry> getEventQueue() {
		return eventQueue;
	}

	@PostConstruct
	protected void initialize() {
		eventQueue = new CircularFifoQueue<LogEntry>(eventBufferSize);
	}

	public void logDetailedEvent(DetailedLogEntry entry) {
		if (isLoggable(entry.getType(), entry.getEventLevel())) {

			reportToSystemLog(entry);

			saveEntry(entry);
		}
	}

	public void logDetailedEvent(LogEntryType type, EventLevel level,
			String context, String event, String text) {
		if (isLoggable(type, level)) {
			DetailedLogEntry entry = new DetailedLogEntry();
			entry.setEvent(event);
			entry.setEventLevel(level);
			entry.setEventText(text);
			entry.setType(type);

			reportToSystemLog(entry);

			saveEntry(entry);
		}

	}

	public void logDetailedEvent(LogEntryType type, EventLevel level,
			String context, String event, String text, Order order) {
		if (isLoggable(type, level)) {
			DetailedLogEntry entry = new DetailedLogEntry();
			entry.setEvent(event);
			entry.setEventLevel(level);
			entry.setEventText(text);
			entry.setType(type);
			entry.setOrder(order);
			reportToSystemLog(entry);

			saveEntry(entry);
		}

	}

	public <T extends Order> void logDetailedEvent(LogEntryType type,
			EventLevel level, String context, String event, String text,
			OrderModel<T> orderModel) {
		if (isLoggable(type, level)) {
			DetailedLogEntry entry = new DetailedLogEntry();
			entry.setEvent(event);
			entry.setEventLevel(level);
			entry.setEventText(text);
			entry.setType(type);
			entry.setOrder(orderModel.getType());

			reportToSystemLog(entry);

			saveEntry(entry);
		}

	}

	public void logExceptionEvent(String context, Exception exp) {
		if (isLoggable(LogEntryType.System_InternalError, EventLevel.error)) {
			SummaryLogEntry entry = new SummaryLogEntry();
			entry.setEvent("Exception");
			entry.setContext(context);
			entry.setEventLevel(EventLevel.error);
			entry.setEventText(exp.getMessage());
			entry.setType(LogEntryType.System_InternalError);
			reportToSystemLog(entry, exp);

			saveEntry(entry);
		}
	}

	public void logExceptionEvent(String context, Exception exp, Order order) {
		if (isLoggable(LogEntryType.System_InternalError, EventLevel.debug)) {
			SummaryLogEntry entry = new SummaryLogEntry();
			entry.setEvent("Exception");
			entry.setContext(context);
			entry.setEventLevel(EventLevel.error);
			entry.setEventText(exp.getMessage());
			entry.setType(LogEntryType.System_InternalError);
			entry.setOrderId(order.getOrderIdentity());
			reportToSystemLog(entry, exp);

			saveEntry(entry);
		}
	}

	public <T extends Order> void logExceptionEvent(String context,
			Exception exp, OrderModel<T> orderModel) {
		if (isLoggable(LogEntryType.System_InternalError, EventLevel.debug)) {
			SummaryLogEntry entry = new SummaryLogEntry();
			entry.setEvent("Exception");
			entry.setContext(context);
			entry.setEventLevel(EventLevel.error);
			entry.setEventText(exp.getMessage());
			entry.setType(LogEntryType.System_InternalError);
			entry.setOrderId(orderModel.getType().getOrderIdentity());
			reportToSystemLog(entry, exp);

			saveEntry(entry);
		}
	}

	public void logSummaryEvent(DetailedLogEntry detailedEntry) {
		if (isLoggable(detailedEntry.getType(), detailedEntry.getEventLevel())) {
			SummaryLogEntry entry = new SummaryLogEntry();
			entry.setEvent(detailedEntry.getEvent());
			entry.setEventLevel(detailedEntry.getEventLevel());
			entry.setEventText(detailedEntry.getEventText());
			entry.setType(detailedEntry.getType());
			reportToSystemLog(entry);

			saveEntry(entry);
		}

	}

	public void logSummaryEvent(LogEntry entry) {
		if (isLoggable(entry.getType(), entry.getEventLevel())) {

			reportToSystemLog(entry);

			saveEntry(entry);
		}

	}

	public void logSummaryEvent(LogEntryType type, EventLevel level,
			String context, String event, String text) {
		if (isLoggable(type, level)) {
			SummaryLogEntry entry = new SummaryLogEntry();
			entry.setEvent(event);
			entry.setEventLevel(level);
			entry.setEventText(text);
			entry.setType(type);

			reportToSystemLog(entry);

			saveEntry(entry);
		}
	}

	public void logSummaryEvent(LogEntryType type, EventLevel level,
			String context, String event, String text, Order order) {
		if (isLoggable(type, level)) {

			SummaryLogEntry entry = new SummaryLogEntry();
			entry.setEvent(event);
			entry.setEventLevel(level);
			entry.setEventText(text);
			entry.setType(type);
			entry.setOrderId(order.getOrderIdentity());
			reportToSystemLog(entry);

			saveEntry(entry);

		}
	}

	public <T extends Order> void logSummaryEvent(LogEntryType type,
			EventLevel level, String context, String event, String text,
			OrderModel<T> orderModel) {
		if (isLoggable(type, level)) {
			SummaryLogEntry entry = new SummaryLogEntry();
			entry.setEvent(event);
			entry.setEventLevel(level);
			entry.setEventText(text);
			entry.setType(type);
			entry.setOrderId(orderModel.getType().getOrderIdentity());
			reportToSystemLog(entry);

			saveEntry(entry);
		}
	}

	public void logSummaryEvent(SummaryLogEntry entry) {
		if (isLoggable(entry.getType(), entry.getEventLevel())) {

			reportToSystemLog(entry);

			saveEntry(entry);
		}

	}

	public void logUserExceptionEvent(String context, Exception exp) {
		if (isLoggable(LogEntryType.User_ExceptionUsingService, EventLevel.warn)) {
			SummaryLogEntry entry = new SummaryLogEntry();
			entry.setEvent("Exception");
			entry.setContext(context);
			entry.setEventLevel(EventLevel.warn);
			entry.setEventText(exp.getMessage());
			entry.setType(LogEntryType.User_ExceptionUsingService);

			reportToSystemLog(entry, exp);

			saveEntry(entry);
		}
	}

	public void logUserExceptionEvent(String context, Exception exp, Order order) {
		if (isLoggable(LogEntryType.User_ExceptionUsingService,
				EventLevel.debug)) {
			SummaryLogEntry entry = new SummaryLogEntry();
			entry.setEvent("Exception");
			entry.setContext(context);
			entry.setEventLevel(EventLevel.warn);
			entry.setEventText(exp.getMessage());
			entry.setType(LogEntryType.User_ExceptionUsingService);

			reportToSystemLog(entry, exp);

			saveEntry(entry);
		}
	}

	public <T extends Order> void logUserExceptionEvent(
			String context, Exception exp, OrderModel<T> orderModel) {
		if (isLoggable(LogEntryType.User_ExceptionUsingService, EventLevel.warn)) {
			SummaryLogEntry entry = new SummaryLogEntry();
			entry.setEvent("Exception");
			entry.setContext(context);
			entry.setEventLevel(EventLevel.warn);
			entry.setEventText(exp.getMessage());
			entry.setType(LogEntryType.User_ExceptionUsingService);
			entry.setOrderId(orderModel.getType().getOrderIdentity());
			reportToSystemLog(entry, exp);

			saveEntry(entry);
		}
	}

	private void logToSLF4J(EventLevel level, String msg) {
		switch (level) {

		case debug: {
			logger.debug(msg);
			break;
		}
		case info: {
			logger.info(msg);
			break;
		}
		case error: {
			logger.error(msg);
			break;
		}
		case warn: {
			logger.warn(msg);
			break;
		}
		default: {
			break;
		}
		}
	}

	private void logToSLF4J(EventLevel level, String msg, Throwable exp) {

		switch (level) {

		case debug: {
			logger.debug(msg, exp);
			break;
		}
		case info: {
			logger.info(msg, exp);
			break;
		}
		case error: {
			logger.error(msg, exp);
			break;
		}
		case warn: {
			logger.warn(msg, exp);
			break;
		}
		case trace: {
			logger.trace(msg, exp);
			break;
		}
		default: {
			logger.trace("Escaped: " + msg, exp);
			break;
		}
		}
	}

	private void reportToSystemLog(DetailedLogEntry ent) {

		logToSLF4J(ent.getEventLevel(),
				String.format("%s - %s", ent.getEvent(), ent.getEventText()));

		// getLogger().info(String.format("DE: %s - %s",
		// ent.getEvent(),ent.getEventText()));
	}

	private void reportToSystemLog(LogEntry ent) {
		logToSLF4J(ent.getEventLevel(),
				String.format("%s - %s", ent.getEvent(), ent.getEventText()));
		// getLogger().info(String.format("LE: %s - %s",
		// ent.getEvent(),ent.getEventText()));
	}

	private void reportToSystemLog(LogEntry ent, Throwable exp) {
		logToSLF4J(ent.getEventLevel(),
				String.format("%s - %s", ent.getEvent(), ent.getEventText()),
				exp);

		// getLogger().info(String.format("EE: %s - %s",
		// ent.getEvent(),ent.getEventText()));
	}

	private void reportToSystemLog(SummaryLogEntry ent) {

		logToSLF4J(ent.getEventLevel(),
				String.format("%s - %s", ent.getEvent(), ent.getEventText()));

		// getLogger().info(String.format("SE: %s - %s",
		// ent.getEvent(),ent.getEventText()));
	}

	private void saveEntry(LogEntry ent) {
		// TODO Save the entry

		// Grab Apache Commons and Get A Circular Buffer for now....
		eventQueue.add(ent);
	}

	/**
	 * @param eventBufferSize
	 *            the eventBufferSize to set
	 */
	public void setEventBufferSize(int eventBufferSize) {

		if (this.eventQueue != null
				&& (this.eventBufferSize != eventBufferSize)) {
			// Resize the buffer as required
			CircularFifoQueue<LogEntry> newQueue = new CircularFifoQueue<LogEntry>(
					eventBufferSize);
			// Move old entries to the new Buffer
			newQueue.addAll(eventQueue);
			eventQueue.clear();
			eventQueue = newQueue;
		}
		this.eventBufferSize = eventBufferSize;
	}

}
