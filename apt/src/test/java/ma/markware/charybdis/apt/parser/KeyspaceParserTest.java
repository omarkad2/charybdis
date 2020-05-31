package ma.markware.charybdis.apt.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptConfiguration;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.AptDefaultConfiguration;
import ma.markware.charybdis.apt.CompilationExtension;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.metatype.KeyspaceMetaType;
import ma.markware.charybdis.model.annotation.Udt;
import ma.markware.charybdis.model.option.Replication;
import ma.markware.charybdis.test.entities.TestKeyspaceDefinition;
import ma.markware.charybdis.test.entities.invalid.DuplicateKeyspaceDefinition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith({CompilationExtension.class})
class KeyspaceParserTest {

  @Mock
  private RoundEnvironment roundEnvironment;

  private AptConfiguration configuration;
  private TypeElement keyspaceTypeElement;

  @BeforeAll
  void setup(Elements elements) {
    MockitoAnnotations.initMocks(this);
    when(roundEnvironment.getElementsAnnotatedWith(Udt.class)).thenReturn(Collections.emptySet());
    keyspaceTypeElement = elements.getTypeElement(TestKeyspaceDefinition.class.getCanonicalName());
  }

  @BeforeEach
  void initProcessorContext(Types types, Elements elements, Filer filer) {
    AptContext aptContext = new AptContext();
    aptContext.init(roundEnvironment, configuration);
    this.configuration = AptDefaultConfiguration.initConfig(aptContext, types, elements, filer);
  }

  @Test
  void parseKeyspaceTest() {
    KeyspaceMetaType keyspaceMetaType = configuration.getKeyspaceParser().parse(keyspaceTypeElement);
    assertThat(keyspaceMetaType.getKeyspaceName()).isEqualTo("test_keyspace");
    assertThat(keyspaceMetaType.getReplication()).isEqualTo(Replication.DEFAULT_REPLICATION);
  }

  @Test
  @DisplayName("Compilation should fail if two keyspaces have the same name")
  void compilation_fails_when_duplicate_keyspace_name(Elements elements) throws IOException {
    configuration.getKeyspaceParser().parse(keyspaceTypeElement);
    assertThrows(CharybdisParsingException.class,
                 () -> configuration.getKeyspaceParser()
                                    .parse(elements.getTypeElement(DuplicateKeyspaceDefinition.class.getCanonicalName())),
                 "keyspace 'test_keyspace' already exist");
  }
}
