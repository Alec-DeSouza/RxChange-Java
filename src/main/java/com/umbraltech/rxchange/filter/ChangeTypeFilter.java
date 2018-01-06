/*
 * Copyright 2018 - present, RxChange contributors
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

package com.umbraltech.rxchange.filter;

import com.umbraltech.rxchange.message.ChangeMessage;
import com.umbraltech.rxchange.type.ChangeType;
import io.reactivex.functions.Predicate;

/**
 * A utility class used for filtering the change type in observers
 * <p>
 * Only messages of the type specified by the filter will be passed to the observers
 */
public class ChangeTypeFilter implements Predicate<ChangeMessage> {
    private final ChangeType changeType;

    /**
     * Constructs the change type filter
     *
     * @param changeType the change type used for filtering
     */
    public ChangeTypeFilter(final ChangeType changeType) {
        this.changeType = changeType;
    }

    @Override
    public boolean test(ChangeMessage changeMessage) {
        return changeMessage.getChangeType() == changeType;
    }
}
