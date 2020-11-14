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

package ma.markware.charybdis.apt.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic.Kind;
import ma.markware.charybdis.apt.exception.CharybdisParsingException;
import ma.markware.charybdis.apt.exception.CharybdisSerializationException;

/**
 * Wrapper that log APT errors before throwing the exception
 *
 * @author Oussama Markad
 */
public class ExceptionMessagerWrapper {

  public static void throwParsingException(Messager messager, String errorMessage, Throwable throwable) {
    messager.printMessage(Kind.ERROR, errorMessage);
    throw new CharybdisParsingException(errorMessage, throwable);
  }

  public static void throwParsingException(Messager messager, String errorMessage) {
    messager.printMessage(Kind.ERROR, errorMessage);
    throw new CharybdisParsingException(errorMessage);
  }

  public static CharybdisParsingException getParsingException(Messager messager, String errorMessage) {
    messager.printMessage(Kind.ERROR, errorMessage);
    return new CharybdisParsingException(errorMessage);
  }

  public static void throwSerializationException(Messager messager, String errorMessage, Throwable throwable) {
    messager.printMessage(Kind.ERROR, errorMessage);
    throw new CharybdisSerializationException(errorMessage, throwable);
  }

  public static void throwSerializationException(Messager messager, String errorMessage) {
    messager.printMessage(Kind.ERROR, errorMessage);
    throw new CharybdisSerializationException(errorMessage);
  }
}
