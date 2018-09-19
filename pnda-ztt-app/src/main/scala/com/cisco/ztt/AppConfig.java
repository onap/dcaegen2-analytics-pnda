/*
 * Copyright (c) 2018 Cisco Systems. All rights reserved.
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

/**
  * Name:       AppConfig
  * Purpose:    Load application properties file.
  * Author:     PNDA team
  *
  * Created:    07/04/2016
  */

package com.cisco.ztt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class AppConfig
{
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class);

    private static Properties _properties;
    private static Object _lock = new Object();

    public static Properties loadProperties()
    {
        synchronized (_lock)
        {
            if (_properties == null)
            {
                _properties = new Properties();
                try
                {
                    InputStream is = AppConfig.class.getClassLoader().getResourceAsStream("application.properties");
                    _properties.load(is);
                    is.close();
                    LOGGER.info("Properties loaded");
                }
                catch (IOException e)
                {
                    LOGGER.info("Failed to load properties", e);
                    System.exit(1);
                }
            }
            return _properties;
        }
    }
}
