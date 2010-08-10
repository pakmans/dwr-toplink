/*
 * Copyright 2009 Daniel Martins
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
package com.destaquenet.dwrtoplink;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;
import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.extend.MarshallException;
import org.directwebremoting.extend.Property;

/**
 * BeanConverter that works with EclipseLink.
 * @author Daniel Martins [daniel at destaquenet dot com]
 */
public class EclipseLinkConverter extends BeanConverter {

    /* (non-Javadoc)
     * @see org.directwebremoting.convert.BeanConverter#getPropertyMapFromObject(java.lang.Object, boolean, boolean)
     */
    @Override
    public Map<String, Property> getPropertyMapFromObject(Object example, boolean readRequired, boolean writeRequired) throws MarshallException {
        Class<?> clazz = example.getClass();
        try {
            BeanInfo info = Introspector.getBeanInfo(clazz);

            Map<String, Property> properties = new HashMap<String, Property>();
            for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
                String name = descriptor.getName();

                // We don't marshall getClass()
                if ("class".equals(name)) {
                    continue;
                }

                // Access rules mean we might not want to do this one
                if (!isAllowedByIncludeExcludeRules(name)) {
                    continue;
                }

                if (readRequired && descriptor.getReadMethod() == null) {
                    continue;
                }

                if (writeRequired && descriptor.getWriteMethod() == null) {
                    continue;
                }

                properties.put(name, new EclipseLinkPropertyDescriptorProperty(descriptor));
            }

            return properties;
        } catch (Exception ex) {
            throw new MarshallException(clazz, ex);
        }
    }
}
