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
package com.google.cloud.dataflow.sdk.options;

import static com.google.cloud.dataflow.sdk.options.DataflowWorkerLoggingOptions.Level.WARN;
import static org.junit.Assert.assertEquals;

import com.google.cloud.dataflow.sdk.options.DataflowWorkerLoggingOptions.WorkerLogLevelOverride;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests for {@link DataflowWorkerLoggingOptions}. */
@RunWith(JUnit4.class)
public class DataflowWorkerLoggingOptionsTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void testWorkerLogLevelOverrideWithInvalidLogLevel() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Unsupported log level");
    WorkerLogLevelOverride.create("Name#FakeLevel");
  }

  @Test
  public void testWorkerLogLevelOverrideForClass() {
    assertEquals("org.junit.Test#WARN",
        MAPPER.convertValue(WorkerLogLevelOverride.forClass(Test.class, WARN), String.class));
  }

  @Test
  public void testWorkerLogLevelOverrideForPackage() {
    assertEquals("org.junit#WARN",
        MAPPER.convertValue(
            WorkerLogLevelOverride.forPackage(Test.class.getPackage(), WARN), String.class));
  }

  @Test
  public void testWorkerLogLevelOverrideForName() {
    assertEquals("A#WARN",
        MAPPER.convertValue(WorkerLogLevelOverride.forName("A", WARN), String.class));
  }

  @Test
  public void testSerializationAndDeserializationOf() {
    String testValue = "A#WARN";
    assertEquals(testValue,
        MAPPER.convertValue(
            MAPPER.convertValue(testValue, WorkerLogLevelOverride.class), String.class));
  }
}
