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
package com.github.charybdis.test.tools;

import com.datastax.oss.driver.api.core.AllNodesFailedException;
import com.datastax.oss.driver.api.core.CqlSession;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DockerClientBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Cassandra docker container.
 *
 * @author Oussama Markad
 */
public class DockerizedCassandra implements AutoCloseable {

  private static final Logger logger = LoggerFactory.getLogger(DockerizedCassandra.class);

  private static final String DOCKER_IMAGE_NAME = "cassandra:3.11.2";
//  private static final String DOCKER_IMAGE_NAME = "scylladb/scylla:latest";
  private static final int CQL_PORT = 9042;
  private static final String DEFAULT_DATACENTER = "datacenter1";

  private final BackoffService backoffService;
  private final DockerClient dockerClient;
  private String containerId;
  private int port;
  private CqlSession session;
  private boolean connection;

  DockerizedCassandra() throws IOException {
    this.dockerClient = DockerClientBuilder.getInstance().build();
    this.backoffService = new BackoffService(10, 10000);
    this.port = findFreePort();
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
  void start() {
    ExposedPort tcp = ExposedPort.tcp(CQL_PORT);

    Ports portBindings = new Ports();
    portBindings.bind(tcp, Ports.Binding.bindPort(port));

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
        connection = true;
      } catch (AllNodesFailedException e) {
        logger.info("Connection failed, will try back in: {}ms. Number of tries left: {}", backoffService.getTimeToWait(), backoffService.getNumberOfTriesLeft());
        backoffService.retry();
      }
    }

    if (!connection) {
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
}
