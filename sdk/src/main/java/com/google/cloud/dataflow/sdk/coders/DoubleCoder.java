/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.dataflow.sdk.coders;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

/**
 * A DoubleCoder encodes Doubles in 8 bytes.
 */
@SuppressWarnings("serial")
public class DoubleCoder extends AtomicCoder<Double> {
  @JsonCreator
  public static DoubleCoder of() {
    return INSTANCE;
  }


  /////////////////////////////////////////////////////////////////////////////

  private static final DoubleCoder INSTANCE = new DoubleCoder();

  private DoubleCoder() {}

  @Override
  public void encode(Double value, OutputStream outStream, Context context)
      throws IOException, CoderException {
    if (value == null) {
      throw new CoderException("cannot encode a null Double");
    }
    new DataOutputStream(outStream).writeDouble(value);
  }

  @Override
  public Double decode(InputStream inStream, Context context)
      throws IOException, CoderException {
    try {
      return new DataInputStream(inStream).readDouble();
    } catch (EOFException | UTFDataFormatException exn) {
      // These exceptions correspond to decoding problems, so change
      // what kind of exception they're branded as.
      throw new CoderException(exn);
    }
  }

  /**
   * Floating-point operations are not guaranteed to be deterministic, even
   * if the storage format might be, so floating point representations are not
   * recommended for use in operations that require deterministic inputs.
   */
  @Override
  @Deprecated
  public boolean isDeterministic() {
    return false;
  }

  @Override
  public void verifyDeterministic() throws NonDeterministicException {
    throw new NonDeterministicException(this,
        "Floating point encodings are not guaranteed to be deterministic.");
  }

  @Override
  public boolean consistentWithEquals() {
    return true;
  }

  /**
   * Returns true since registerByteSizeObserver() runs in constant time.
   */
  @Override
  public boolean isRegisterByteSizeObserverCheap(Double value, Context context) {
    return true;
  }

  @Override
  protected long getEncodedElementByteSize(Double value, Context context)
      throws Exception {
    if (value == null) {
      throw new CoderException("cannot encode a null Double");
    }
    return 8;
  }
}
