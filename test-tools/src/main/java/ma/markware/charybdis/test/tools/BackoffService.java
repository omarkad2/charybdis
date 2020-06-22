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
package ma.markware.charybdis.test.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BackoffService {

  private static final Logger logger = LoggerFactory.getLogger(BackoffService.class);

  private int numberOfTriesLeft;
  private long timeToWait;

  BackoffService(int numberOfRetries, long defaultTimeToWait){
    this.numberOfTriesLeft = numberOfRetries;
    this.timeToWait = defaultTimeToWait;
  }

  boolean shouldRetry() {
    return numberOfTriesLeft > 0;
  }

  void retry() {
    numberOfTriesLeft --;
    if (!shouldRetry()) {
      logger.info("*** RETRY FAILED ***");
    }
    waitUntilNextTry();
  }

  private void waitUntilNextTry() {
    try {
      Thread.sleep(timeToWait);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  void doNotRetry() {
    numberOfTriesLeft = 0;
  }

  int getNumberOfTriesLeft() {
    return numberOfTriesLeft;
  }

  long getTimeToWait() {
    return timeToWait;
  }
}
