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
package ma.markware.charybdis.apt.parser;

import static ma.markware.charybdis.apt.utils.ExceptionMessagerWrapper.throwParsingException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Types;
import ma.markware.charybdis.apt.AptContext;
import ma.markware.charybdis.apt.metatype.AbstractEntityMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;
import ma.markware.charybdis.apt.metatype.UdtFieldMetaType;
import ma.markware.charybdis.apt.metatype.UdtMetaType;
import ma.markware.charybdis.apt.utils.ParserUtils;
import ma.markware.charybdis.model.annotation.Udt;

/**
 * A specific Class parser.
 * Parses classes annotated with {@link ma.markware.charybdis.model.annotation.Udt}.
 *
 * @author Oussama Markad
 */
public class UdtParser extends AbstractEntityParser<UdtMetaType> {

  private final FieldParser<UdtFieldMetaType> udtFieldParser;
  private final AptContext aptContext;
  private final Types types;

  public UdtParser(final FieldParser<UdtFieldMetaType> udtFieldParser, final AptContext aptContext, final Types types, final Messager messager) {
    super(messager);
    this.udtFieldParser = udtFieldParser;
    this.aptContext = aptContext;
    this.types = types;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UdtMetaType parse(final Element annotatedClass) {
    validateMandatoryConstructors(annotatedClass, messager);

    final Udt udt = annotatedClass.getAnnotation(Udt.class);

    AbstractEntityMetaType abstractEntityMetaType = parseGenericEntity(annotatedClass, udt.keyspace(), aptContext);
    final UdtMetaType udtMetaType = new UdtMetaType(abstractEntityMetaType);

    String udtName = resolveName(annotatedClass);
    validateName(udtName, messager);
    udtMetaType.setUdtName(udtName);

    Stream<? extends Element> fields = ParserUtils.extractFields(annotatedClass, types);

    List<UdtFieldMetaType> udtFields = fields.map(fieldElement -> udtFieldParser.parse(annotatedClass, fieldElement, udtMetaType.getUdtName()))
                                             .filter(Objects::nonNull)
                                             .collect(Collectors.toList());
    udtMetaType.setUdtFields(udtFields);

    validateNestedUdtFields(udtFields);

    return udtMetaType;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String resolveName(final Element annotatedClass) {
    final Udt table = annotatedClass.getAnnotation(Udt.class);
    return resolveName(table.name(), annotatedClass.getSimpleName());
  }

  private void validateNestedUdtFields(final List<UdtFieldMetaType> udtFields) {
    udtFields.stream()
             .filter(udtField -> udtField.getFieldType().getFieldTypeKind() == FieldTypeKind.UDT && !udtField.getFieldType().isFrozen())
             .findAny().ifPresent(invalidUdt ->
                                      throwParsingException(messager, String.format("Nested UDT Field '%s' should be annotated with @Frozen",
                                                                                    invalidUdt.getDeserializationName()))
    );
  }
}
