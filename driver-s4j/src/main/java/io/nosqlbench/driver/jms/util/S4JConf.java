/*
 * Copyright (c) 2022 nosqlbench
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nosqlbench.driver.jms.util;

/*
 * Copyright (c) 2022 nosqlbench
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class S4JConf {
    private final static Logger logger = LogManager.getLogger(S4JConf.class);

    public static final String CLIENT_CONF_PREFIX = "client";
    public static final String PRODUCER_CONF_PREFIX = "producer";
    public static final String CONSUMER_CONF_PREFIX = "consumer";
    public static final String JMS_CONF_PREFIX = "jms";

    private final Map<String, Object> s4jMiscConfMap = new HashMap<>();
    private final Map<String, Object> clientConfMap = new HashMap<>();
    private final Map<String, Object> producerConfMap = new HashMap<>();
    private final Map<String, Object> consumerConfMap = new HashMap<>();
    private final Map<String, Object> jmsConfMap = new HashMap<>();


    public S4JConf(String fileName) {
        File file = new File(fileName);

        try {
            String canonicalFilePath = file.getCanonicalPath();

            Parameters params = new Parameters();

            FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                    .configure(params.properties()
                        .setFileName(fileName));

            Configuration config = builder.getConfiguration();

            for (Iterator<String> it = config.getKeys(); it.hasNext(); ) {
                String confKey = it.next();
                String confVal = config.getProperty(confKey).toString();

                if (!StringUtils.isBlank(confVal)) {
                    // Get client connection specific configuration settings, removing "client." prefix
                    if (StringUtils.startsWith(confKey, CLIENT_CONF_PREFIX)) {
                        clientConfMap.put(confKey.substring(CLIENT_CONF_PREFIX.length() + 1), config.getProperty(confKey));
                    }
                    // Get producer specific configuration settings, removing "producer." prefix
                    else if (StringUtils.startsWith(confKey, PRODUCER_CONF_PREFIX)) {
                        producerConfMap.put(confKey.substring(PRODUCER_CONF_PREFIX.length() + 1), config.getProperty(confKey));
                    }
                    // Get consumer specific configuration settings, removing "consumer." prefix
                    else if (StringUtils.startsWith(confKey, CONSUMER_CONF_PREFIX)) {
                        consumerConfMap.put(confKey.substring(CONSUMER_CONF_PREFIX.length() + 1), config.getProperty(confKey));
                    }
                    // Get JMS specific configuration settings, keeping "jms." prefix
                    else if (StringUtils.startsWith(confKey, JMS_CONF_PREFIX)) {
                        jmsConfMap.put(confKey, config.getProperty(confKey));
                    }
                    // For all other configuration settings (not having any of the above prefixes), keep as is
                    else {
                        s4jMiscConfMap.put(confKey, confVal);
                    }
                }
            }
        } catch (IOException ioe) {
            logger.error("Can't read the specified config properties file: " + fileName);
            ioe.printStackTrace();
        } catch (ConfigurationException cex) {
            logger.error("Error loading configuration items from the specified config properties file: " + fileName + ":" + cex.getMessage());
            cex.printStackTrace();
        }
    }

    public Map<String, Object> getS4jMiscConfMap() { return  this.s4jMiscConfMap; }
    public Map<String, Object> getClientConfMap() { return this.clientConfMap; }
    public Map<String, Object> getProducerConfMap() { return this.producerConfMap; }
    public Map<String, Object> getConsumerConfMap() { return this.consumerConfMap; }
    public Map<String, Object> getJmsConfMap() { return this.jmsConfMap; }

    public String toString() {
        return new ToStringBuilder(this).
            append("s4jMiscConfMap", s4jMiscConfMap.toString()).
            append("clientConfMap", clientConfMap.toString()).
            append("producerConfMap", producerConfMap.toString()).
            append("consumerConfMap", consumerConfMap.toString()).
            append("jmsConfMap", jmsConfMap.toString()).
            toString();
    }
}