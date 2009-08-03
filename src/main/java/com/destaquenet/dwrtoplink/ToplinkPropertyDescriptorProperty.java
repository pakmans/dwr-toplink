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

import java.beans.PropertyDescriptor;
import oracle.toplink.essentials.exceptions.ValidationException;
import oracle.toplink.essentials.indirection.IndirectContainer;
import org.directwebremoting.extend.MarshallException;
import org.directwebremoting.impl.PropertyDescriptorProperty;

/**
 * A {@link Property} that catches Toplink exceptions.
 * This is useful for Toplink  where lazy loading results in an exception
 * and you are unable to detect and prevent this.
 * @author Daniel Martins [daniel at destaquenet dot com]
 */
public class ToplinkPropertyDescriptorProperty extends PropertyDescriptorProperty {

    /**
     * Simple constructor
     * @param descriptor The PropertyDescriptor that we are proxying to
     */
    public ToplinkPropertyDescriptorProperty(PropertyDescriptor descriptor) {
        super(descriptor);
    }

    /* (non-Javadoc)
     * @see org.directwebremoting.impl.PropertyDescriptorProperty#getValue(java.lang.Object)
     */
    @Override
    public Object getValue(Object bean) throws MarshallException {
        try {
            Object value = super.getValue(bean);

            // Only serializes a lazy collection if it's already initialized
            if (value instanceof IndirectContainer) {
                if (!((IndirectContainer) value).isInstantiated()) {
                    throw new MarshallException(bean.getClass(),
                            ValidationException.instantiatingValueholderWithNullSession());
                }
            }
            return value;
        } catch (MarshallException ex) {
            if (ex.getCause() instanceof ValidationException) {
                if (((ValidationException) ex.getCause()).getErrorCode() ==
                        ValidationException.INSTANTIATING_VALUEHOLDER_WITH_NULL_SESSION) {
                    return null;
                }
            }
            throw ex;
        } catch (Exception ex) {
            throw new MarshallException(bean.getClass(), ex);
        }
    }
}
