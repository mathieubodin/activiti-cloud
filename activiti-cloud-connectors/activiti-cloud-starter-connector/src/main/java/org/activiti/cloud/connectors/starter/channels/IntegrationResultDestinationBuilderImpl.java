package org.activiti.cloud.connectors.starter.channels;

import org.activiti.cloud.api.process.model.IntegrationRequest;
import org.activiti.cloud.connectors.starter.configuration.ConnectorProperties;

public class IntegrationResultDestinationBuilderImpl implements IntegrationResultDestinationBuilder {

    private final ConnectorProperties connectorProperties;

    public IntegrationResultDestinationBuilderImpl(ConnectorProperties connectorProperties) {
        this.connectorProperties = connectorProperties;
    }

    @Override
    public String buildDestination(IntegrationRequest event) {
        String resultDestinationOverride = connectorProperties.getErrorDestinationOverride();

        String destination = (resultDestinationOverride == null || resultDestinationOverride.isEmpty())
                ? "integrationResult" + connectorProperties.getMqDestinationSeparator() + event.getServiceFullName() : resultDestinationOverride;

        return destination;
    }

}
