/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.services.events.listeners;

import org.activiti.cloud.services.events.converter.ToCloudProcessRuntimeEventConverter;
import org.activiti.runtime.api.event.ProcessCancelledEvent;
import org.activiti.runtime.api.event.ProcessCompletedEvent;
import org.activiti.runtime.api.event.ProcessCreatedEvent;
import org.activiti.runtime.api.event.ProcessResumedEvent;
import org.activiti.runtime.api.event.ProcessStartedEvent;
import org.activiti.runtime.api.event.ProcessSuspendedEvent;
import org.activiti.runtime.api.event.listener.ProcessRuntimeEventListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessRuntimeExternalEventsProducer implements ProcessRuntimeEventListener {

    private final ToCloudProcessRuntimeEventConverter eventConverter;
    private final ProcessEngineEventsAggregator eventsAggregator;

    public ProcessRuntimeExternalEventsProducer(ToCloudProcessRuntimeEventConverter eventConverter,
                                                ProcessEngineEventsAggregator eventsAggregator) {
        this.eventConverter = eventConverter;
        this.eventsAggregator = eventsAggregator;
    }

    @Override
    public void onProcessCreated(ProcessCreatedEvent event) {
        eventsAggregator.add(eventConverter.from(event));
    }

    @Override
    public void onProcessStarted(ProcessStartedEvent event) {
        eventsAggregator.add(eventConverter.from(event));
    }

    @Override
    public void onProcessSuspended(ProcessSuspendedEvent event) {
        eventsAggregator.add(eventConverter.from(event));
    }

    @Override
    public void onProcessResumed(ProcessResumedEvent event) {
        eventsAggregator.add(eventConverter.from(event));
    }

    @Override
    public void onProcessCompleted(ProcessCompletedEvent event) {
        eventsAggregator.add(eventConverter.from(event));
    }

    @Override
    public void onProcessCancelled(ProcessCancelledEvent event) {
        eventsAggregator.add(eventConverter.from(event));
    }
}
