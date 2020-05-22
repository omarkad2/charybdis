package ma.markware.charybdis.apt.parser;

import com.squareup.javapoet.TypeName;
import java.util.Arrays;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.FieldTypeKind;
import ma.markware.charybdis.apt.metatype.FieldTypeMetaType.TypeDetail;

class ParserTestHelper {

  static FieldTypeMetaType buildFieldTypeMetaType(TypeName serializationTypeName, FieldTypeKind fieldTypeKind) {
    return new FieldTypeMetaType(TypeDetail.from(serializationTypeName), TypeDetail.from(serializationTypeName), fieldTypeKind,
                                 false, false, false);
  }

  static FieldTypeMetaType buildFieldTypeMetaType(TypeName serializationTypeName, TypeName deserializationTypeName, FieldTypeKind fieldTypeKind,
      boolean frozen, boolean custom, boolean complex, FieldTypeMetaType... fieldTypeMetaTypes ) {
    FieldTypeMetaType fieldTypeMetaType = new FieldTypeMetaType(TypeDetail.from(serializationTypeName), TypeDetail.from(deserializationTypeName),
                                                                fieldTypeKind, frozen, custom, complex);
    fieldTypeMetaType.setSubTypes(Arrays.asList(fieldTypeMetaTypes));
    return fieldTypeMetaType;
  }

  static FieldTypeMetaType buildFieldTypeMetaType(TypeName serializationTypeName, FieldTypeKind fieldTypeKind,
      boolean frozen, boolean complex, FieldTypeMetaType... fieldTypeMetaTypes) {
    FieldTypeMetaType fieldTypeMetaType = new FieldTypeMetaType(TypeDetail.from(serializationTypeName), TypeDetail.from(serializationTypeName),
                                                                fieldTypeKind, frozen, false, complex);
    fieldTypeMetaType.setSubTypes(Arrays.asList(fieldTypeMetaTypes));
    return fieldTypeMetaType;
  }
}
