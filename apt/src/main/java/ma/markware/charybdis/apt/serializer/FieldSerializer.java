package ma.markware.charybdis.apt.serializer;

import com.squareup.javapoet.FieldSpec;

public interface FieldSerializer<FIELD_META_TYPE> {

  FieldSpec serializeField(FIELD_META_TYPE fieldMetaType);

  FieldSpec serializeFieldGenericType(FIELD_META_TYPE fieldMetaType);
}
