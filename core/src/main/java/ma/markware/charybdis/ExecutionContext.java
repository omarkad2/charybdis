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
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import ma.markware.charybdis.model.option.ConsistencyLevel;

/**
 * A POJO that handles specific options for query executions
 *
 * @author Oussama Markad
 */
public class ExecutionContext {

  private ConsistencyLevel consistencyLevel;
  private ConsistencyLevel defaultConsistencyLevel;
  private DriverExecutionProfile driverExecutionProfile;
  private String executionProfileName;

  public ExecutionContext() {
  }

  public ExecutionContext(ExecutionContext source) {
    consistencyLevel = source.consistencyLevel;
    driverExecutionProfile = source.driverExecutionProfile;
    executionProfileName = source.executionProfileName;
    defaultConsistencyLevel = source.defaultConsistencyLevel;
  }

  @VisibleForTesting
  public ExecutionContext(final ConsistencyLevel consistencyLevel, final ConsistencyLevel defaultConsistencyLevel,
      final DriverExecutionProfile driverExecutionProfile, final String executionProfileName) {
    this.consistencyLevel = consistencyLevel;
    this.defaultConsistencyLevel = defaultConsistencyLevel;
    this.driverExecutionProfile = driverExecutionProfile;
    this.executionProfileName = executionProfileName;
  }

  public ConsistencyLevel getConsistencyLevel() {
    return consistencyLevel;
  }

  public void setConsistencyLevel(ConsistencyLevel consistencyLevel) {
    this.consistencyLevel = consistencyLevel;
  }

  public ConsistencyLevel getDefaultConsistencyLevel() {
    return defaultConsistencyLevel;
  }

  public void setDefaultConsistencyLevel(ConsistencyLevel defaultConsistencyLevel) {
    this.defaultConsistencyLevel = defaultConsistencyLevel;
  }

  public DriverExecutionProfile getDriverExecutionProfile() {
    return driverExecutionProfile;
  }

  public void setDriverExecutionProfile(DriverExecutionProfile driverExecutionProfile) {
    this.driverExecutionProfile = driverExecutionProfile;
  }

  public String getExecutionProfileName() {
    return executionProfileName;
  }

  public void setExecutionProfileName(String executionProfileName) {
    this.executionProfileName = executionProfileName;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ExecutionContext)) {
      return false;
    }
    final ExecutionContext that = (ExecutionContext) o;
    return consistencyLevel == that.consistencyLevel && defaultConsistencyLevel == that.defaultConsistencyLevel && Objects.equals(
        driverExecutionProfile, that.driverExecutionProfile) && Objects.equals(executionProfileName, that.executionProfileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consistencyLevel, defaultConsistencyLevel, driverExecutionProfile, executionProfileName);
  }

  @Override
  public String toString() {
    return "ExecutionContext{" + "consistencyLevel=" + consistencyLevel + ", defaultConsistencyLevel=" + defaultConsistencyLevel
        + ", driverExecutionProfile=" + driverExecutionProfile + ", executionProfileName='" + executionProfileName + '\'' + '}';
  }
}
