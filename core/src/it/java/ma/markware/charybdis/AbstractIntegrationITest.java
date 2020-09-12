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


import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.term.Term;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import ma.markware.charybdis.test.tools.DatabaseSetupExtension;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(DatabaseSetupExtension.class)
public class AbstractIntegrationITest {

  private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationITest.class);

  @BeforeAll
  static void initDb(CqlSession session) throws IOException {
    logger.info("Start creating Keyspaces/Udts/Tables");
    InputStream resourceAsStream = AbstractIntegrationITest.class.getClassLoader().getResourceAsStream("ddl_create_int.cql");
    assert resourceAsStream != null;
    StringWriter writer = new StringWriter();
    IOUtils.copy(resourceAsStream, writer, StandardCharsets.UTF_8);
    String[] statements = StringUtils.split(writer.toString(), ";\n");
    Arrays.stream(statements).filter(StringUtils::isNotBlank).map(statement -> StringUtils.normalizeSpace(statement) + ";\n").forEach(session::execute);
    logger.info("End creating Keyspaces/Udts/Tables");
  }

  protected void insertRow(CqlSession session, String keyspaceName, String tableName, Map<String, Term> values) {
    session.execute(QueryBuilder.insertInto(keyspaceName, tableName).values(values).build());
  }

  protected void insertRow(CqlSession session, String keyspaceName, String tableName, Map<String, Term> values, int ttlInSeconds) {
    session.execute(QueryBuilder.insertInto(keyspaceName, tableName).values(values).usingTtl(ttlInSeconds).build());
  }

  protected void insertRow(CqlSession session, String keyspaceName, String tableName, Map<String, Term> values, long timestamp) {
    session.execute(QueryBuilder.insertInto(keyspaceName, tableName).values(values).usingTimestamp(timestamp).build());
  }

  protected void cleanDatabase(CqlSession session) {
    logger.info("Cleaning database");
    session.execute(SimpleStatement.builder("TRUNCATE test_keyspace.test_entity;").build());
    session.execute(SimpleStatement.builder("TRUNCATE test_keyspace.test_entity_by_date;").build());
  }
}
