/*
 * Copyright 2019 Alfresco, Inc. and/or its affiliates.
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

package org.activiti.cloud.starter.tests.swagger;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.activiti.spring.ProcessDeployedEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class RuntimeBundleSwaggerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessDeployedEventProducer producer;

    @Test
    public void should_swaggerDefinitionHavePathsAndDefinitionsAndInfo() throws Exception {
        mockMvc.perform(get("/v2/api-docs").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.paths").isNotEmpty())
            .andExpect(jsonPath("$.definitions").isNotEmpty())
            .andExpect(jsonPath("$.definitions").value(hasKey(startsWith("ListResponseContent"))))
            .andExpect(jsonPath("$.definitions").value(hasKey(startsWith("EntriesResponseContent"))))
            .andExpect(jsonPath("$.definitions").value(hasKey(startsWith("EntryResponseContent"))))
            .andExpect(jsonPath("$.definitions[\"SaveTaskPayload\"].properties").value(hasKey("payloadType")))
            .andExpect(jsonPath("$.info.title").value("Activiti Cloud Starter :: Runtime Bundle ReST API"));
    }

}
