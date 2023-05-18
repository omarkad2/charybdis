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

import com.datastax.oss.driver.api.core.AllNodesFailedException;
import com.datastax.oss.driver.api.core.CqlSession;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.DockerClientException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Cassandra docker container.
 *
 * @author Oussama Markad
 */
public class DockerizedCassandra implements ExtensionContext.Store.CloseableResource {

  private static final Logger logger = LoggerFactory.getLogger(DockerizedCassandra.class);

  // Docker image
  private static final String DOCKER_IMAGE_NAME = "cassandra:3.11";
//  private static final String DOCKER_IMAGE_NAME = "scylladb/scylla";

  // Datastax properties
  private static final String REQUEST_TIMEOUT_PROPERTY = "datastax-java-driver.basic.request.timeout";
  private static final String CONTACT_POINT_PROPERTY = "datastax-java-driver.basic.contact-points.0";
  private static final String LOCAL_DATACENTER_PROPERTY = "datastax-java-driver.basic.load-balancing-policy.local-datacenter";

  // Values
  private static final int CQL_PORT = 9042;
  private static final String DEFAULT_DATACENTER = "datacenter1";

  private final DockerClient dockerClient;
  private final BackoffService backoffService;
  private String containerId;
  private int port;
  private CqlSession session;
  private boolean isUp;

  DockerizedCassandra() throws IOException {
    this.dockerClient = DockerClientBuilder.getInstance().build();
    this.backoffService = new BackoffService(10, 10000);
    this.port = findFreePort();
    this.isUp = false;
    System.setProperty(REQUEST_TIMEOUT_PROPERTY, "1 minute");
    System.setProperty(CONTACT_POINT_PROPERTY, "127.0.0.1:" + port);
    System.setProperty(LOCAL_DATACENTER_PROPERTY, DEFAULT_DATACENTER);
    start();
  }

  public CqlSession getSession() {
    return session;
  }

  int getPort() {
    return port;
  }

  /**
   * Start docker image.
   */
  private void start() {
    ExposedPort tcp = ExposedPort.tcp(CQL_PORT);

    Ports portBindings = new Ports();
    portBindings.bind(tcp, Ports.Binding.bindPort(port));

    checkAndPullImage(DOCKER_IMAGE_NAME);
    CreateContainerResponse containerResponse = dockerClient.createContainerCmd(DOCKER_IMAGE_NAME)
      .withExposedPorts(tcp)
      .withHostConfig(
        HostConfig.newHostConfig().withPortBindings(portBindings)
      )
      .exec();
    logger.info("Created container : {}", containerResponse);

    containerId = containerResponse.getId();
    dockerClient.startContainerCmd(containerId)
      .exec();

    while (backoffService.shouldRetry()) {
      try {
        session = CqlSession.builder()
          .addContactPoint(new InetSocketAddress(port))
          .withLocalDatacenter(DEFAULT_DATACENTER)
          .build();
        backoffService.doNotRetry();
        isUp = true;
      } catch (AllNodesFailedException e) {
        logger.info("Connection failed, will try back in: {}ms. Number of tries left: {}", backoffService.getTimeToWait(), backoffService.getNumberOfTriesLeft());
        backoffService.retry();
      }
    }

    if (!isUp) {
      throw new IllegalStateException("Couldn't establish connection with cassandra instance");
    }
    logger.info("Connection established with success");
  }

  /**
   * Stop docker image.
   */
  @Override
  public synchronized void close() {
    if (session != null && !session.isClosed()) {
      session.close();
    }
    if (isContainerExists(dockerClient, containerId)) {
      logger.info("Kill & Remove Container");
      dockerClient.killContainerCmd(containerId).exec();
      dockerClient.removeContainerCmd(containerId).exec();
      System.clearProperty(REQUEST_TIMEOUT_PROPERTY);
      System.clearProperty(CONTACT_POINT_PROPERTY);
      System.clearProperty(LOCAL_DATACENTER_PROPERTY);
    }
  }

  private boolean isContainerExists(DockerClient dockerClient, String containerId) {
    List<Container> containers = dockerClient.listContainersCmd()
      .exec();
    logger.info("Containers started : " + containers.stream().map(Container::toString).collect(Collectors.joining(", ")));
    return containers.stream().anyMatch(container -> Objects.equals(containerId, container.getId()));
  }

  private static int findFreePort() throws IOException {
    try (ServerSocket socket = new ServerSocket(0)) {
      socket.setReuseAddress(true);
      return socket.getLocalPort();
    }
  }

  public void checkAndPullImage(String image) {
    try {
      try {
        dockerClient.inspectImageCmd(image).exec();
      } catch (NotFoundException notFoundException) {
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
        try {
          pullImageCmd.exec(new PullImageResultCallback()).awaitCompletion();
        } catch (DockerClientException e) {
          // Try to fallback to x86
          pullImageCmd
            .withPlatform("linux/amd64")
            .exec(new PullImageResultCallback())
            .awaitCompletion();
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
