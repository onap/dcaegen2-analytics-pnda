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
  * Name:       StaticHelpers
  * Purpose:    Helper functions
  * Author:     PNDA team
  *
  * Created:    07/04/2016
  */

package com.cisco.pnda.model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class StaticHelpers {
	public static String loadResourceFile(String path)
    {
        InputStream is = StaticHelpers.class.getClassLoader().getResourceAsStream(path);
        try
        {
            char[] buf = new char[2048];
            Reader r = new InputStreamReader(is, "UTF-8");
            StringBuilder s = new StringBuilder();
            while (true)
            {
                int n = r.read(buf);
                if (n < 0)
                    break;
                s.append(buf, 0, n);
            }
            return s.toString();
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
