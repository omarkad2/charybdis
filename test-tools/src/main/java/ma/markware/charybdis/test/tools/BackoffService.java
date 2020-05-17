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
