/*
 * Charybdis - Cassandra ORM framework
 *
 * Copyright (C) 2020 Charybdis authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ma.markware.charybdis;

import com.datastax.oss.driver.api.core.config.DriverExecutionProfile;

/**
 * A class implements the {@link ExecutionProfileTunable} interface to indicate that it can modify execution provile of a query.
 *
 * @param <T> query builder type.
 *
 * @author Oussama Markad
 */
public interface ExecutionProfileTunable<T extends QueryBuilder> {

  /**
   * Set execution profile that will be applied to queries by our entity manager.
   *
   * @param executionProfile driver execution profile.
   * @return a new entity manager instance with a specific execution profile.
   */
  T withExecutionProfile(DriverExecutionProfile executionProfile);

  /**
   * Set execution profile that will be applied to queries by our entity manager.
   *
   * @param executionProfile execution profile name.
   * @return a new entity manager instance with a specific execution profile.
   */
  T withExecutionProfile(String executionProfile);
}
