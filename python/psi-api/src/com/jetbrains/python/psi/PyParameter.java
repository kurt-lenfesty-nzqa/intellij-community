// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.jetbrains.python.psi;

import com.jetbrains.python.PyNames;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract function parameter; may cover either a named parameter or a tuple of parameters.
 * @see com.jetbrains.python.psi.impl.ParamHelper
 * User: dcheryasov
 * Date: Jul 5, 2009 8:30:13 PM
 */
public interface PyParameter extends PyElement {

  /**
   * @return the named parameter which is represented by this parameter, or null if the parameter is a tuple.
   */
  @Nullable
  PyNamedParameter getAsNamed();

  /**
   * @return the tuple parameter which is represented by this parameter, or null if the parameter is named.
   */
  @Nullable
  PyTupleParameter getAsTuple();

  @Nullable
  PyExpression getDefaultValue();

  boolean hasDefaultValue();

  /**
   * @deprecated Use {@link PyParameter#getDefaultValueText()} instead.
   * This method will be removed in 2018.2.
   */
  @Deprecated
  default boolean hasDefaultNoneValue() {
    return PyNames.NONE.equals(getDefaultValueText());
  }

  /**
   * @apiNote This method will be marked as abstract in 2018.2.
   */
  @Nullable
  default String getDefaultValueText() {
    return null;
  }

  /**
   * @return true if the parameter is the 'self' parameter of an instance attribute function or a function
   * annotated with @classmethod
   */
  boolean isSelf();
}
