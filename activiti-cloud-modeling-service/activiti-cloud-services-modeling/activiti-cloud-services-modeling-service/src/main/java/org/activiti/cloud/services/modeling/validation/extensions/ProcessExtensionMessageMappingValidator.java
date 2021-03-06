package org.activiti.cloud.services.modeling.validation.extensions;

import org.activiti.bpmn.model.*;
import org.activiti.cloud.modeling.api.ModelValidationError;
import org.activiti.cloud.modeling.api.ValidationContext;
import org.activiti.cloud.modeling.api.process.Extensions;
import org.activiti.cloud.modeling.api.process.ProcessVariableMapping;
import org.activiti.cloud.modeling.api.process.ServiceTaskActionType;
import org.activiti.cloud.services.modeling.converter.BpmnProcessModelContent;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

/**
 * Implementation of {@link ProcessExtensionsValidator} for validating message payload name
 */
public class ProcessExtensionMessageMappingValidator implements ProcessExtensionsValidator {

    private static final String PAYLOAD_PATTERN = "[a-z]([a-zA-Z0-9]+)?";
    private static final String INVALID_PAYLOAD_NAME_ERROR = "Invalid %s message payload name in process extensions: %s";
    private static final String INVALID_PAYLOAD_NAME_ERROR_DESCRIPTION =
        "The extensions for process '%s' contains mappings to element '%s' for an invalid payload name '%s'";


    @Override
    public Stream<ModelValidationError> validateExtensions(Extensions extensions, BpmnProcessModelContent bpmnModel, ValidationContext validationContext) {
        Set<FlowNode> messageElements = bpmnModel.findAllNodes(
            StartEvent.class,
            IntermediateCatchEvent.class,
            BoundaryEvent.class,
            ThrowEvent.class,
            EndEvent.class).stream()
            .filter(element -> isMessageElement((Event) element))
            .collect(Collectors.toSet());

        return extensions.getVariablesMappings().entrySet().stream().flatMap(taskMapping ->
            validateMapping(bpmnModel.getId(), taskMapping.getKey(), taskMapping.getValue(), messageElements));
    }

    private Stream<ModelValidationError> validateMapping(String processId,
                                                         String elementId,
                                                         Map<ServiceTaskActionType, Map<String, ProcessVariableMapping>> extensionMapping,
                                                         Set<FlowNode> messages) {

        FlowNode flowNode = messages.stream().filter(message -> message.getId().equals(elementId)).findFirst().orElse(null);

        Stream<ModelValidationError> errorStream = Stream.of();
        if (flowNode != null) {
            errorStream = extensionMapping.entrySet().stream()
                .map(mappingEntry -> new MappingModel(processId, flowNode, mappingEntry.getKey(), mappingEntry.getValue()))
                .filter(mappingModel -> mappingModel.getAction() == ServiceTaskActionType.INPUTS)
                .map(this::validate)
                .flatMap(Collection::stream);
        }

        return errorStream;
    }

    private List<ModelValidationError> validate(MappingModel mappingModel) {
        return mappingModel.getProcessVariableMappings().entrySet().stream()
            .filter((variable) -> !variable.getKey().matches(PAYLOAD_PATTERN))
            .map(variable -> createModelValidationError(
                format(INVALID_PAYLOAD_NAME_ERROR, mappingModel.getFlowNode().getName(), mappingModel.getProcessId()),
                format(INVALID_PAYLOAD_NAME_ERROR_DESCRIPTION, mappingModel.getProcessId(), mappingModel.getFlowNode().getId(), variable.getKey())
            ))
            .collect(Collectors.toList());
    }

    private boolean isMessageElement(Event element) {
        if (element.getEventDefinitions() != null && element.getEventDefinitions().size() > 0) {
            return element.getEventDefinitions().stream().filter(definition -> definition instanceof MessageEventDefinition).toArray().length > 0;
        }
        return false;
    }

}

