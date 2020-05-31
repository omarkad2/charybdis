package ma.markware.charybdis.apt;

class ProcessorSerializerTest {

//  private static final String TARGET = "serialization-test";
//
//  private Path sourceFolderPath;
//  private Path generatedFolderPath;
//  private Path expectedFolderPath;
//
//  @BeforeEach
//  void setup() {
//    String packagePath = getClass().getPackage().getName().replace(".", "/") + "/domain";
//    sourceFolderPath = Paths.get("src/test/java/" + packagePath);
//    generatedFolderPath = Paths.get(format("target/%s/%s", TARGET, packagePath));
//    expectedFolderPath = Paths.get("src/test/resources/generated");
//  }
//
//  @Test
//  void processorSerializationTest() throws IOException {
//    File keyspaceSource = sourceFolderPath.resolve("TestKeyspaceDefinition.java").toFile();
//    File addressSource = sourceFolderPath.resolve("Address.java").toFile();
//    File countrySource = sourceFolderPath.resolve("Country.java").toFile();
//    File userSource = sourceFolderPath.resolve("User.java").toFile();
//    List<String> sources = Stream.of(keyspaceSource, addressSource, countrySource, userSource)
//                                 .map(File::getPath)
//                                 .collect(Collectors.toList());
//
//    // Compilation succeeds
//    CompilationResult processResult = process(sources, TARGET);
//    assertThat(processResult.getCode()).withFailMessage(processResult.getErr())
//                                       .isEqualTo(0);
//
//    assertThatFileIsGeneratedAsExpected("TestKeyspaceDefinition_Keyspace");
//    assertThatFileIsGeneratedAsExpected("Country_Udt");
//    assertThatFileIsGeneratedAsExpected("Address_Udt");
//    assertThatFileIsGeneratedAsExpected("User_Table");
//  }
//
//  private void assertThatFileIsGeneratedAsExpected(final String fileName) throws IOException {
//    String generatedFileContent = extractFileContent(generatedFolderPath.resolve(fileName + ".java"));
//    String expectedFileContent = extractFileContent(expectedFolderPath.resolve(fileName + ".generated"));
//
//    assertThat(generatedFileContent).withFailMessage("actual file:\n'\n%s'\n\nexpected file:\n'\n%s'", generatedFileContent, expectedFileContent)
//                                    .isEqualTo(expectedFileContent);
//  }
//
//  private String extractFileContent(Path filePath) throws IOException {
//    byte[] fileContentAsBytes = Files.readAllBytes(filePath);
//    String fileContentAsString = new String(fileContentAsBytes, StandardCharsets.UTF_8);
//
//    if (!fileContentAsString.contains("\r\n")) {
//      // file is not in DOS format
//      // convert it first, so that the content comparison will be relevant
//      fileContentAsString = fileContentAsString.replaceAll("\n", "\r\n");
//    }
//    return fileContentAsString;
//  }
}
