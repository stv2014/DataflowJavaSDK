/*
 * Copyright (C) 2015 The Google Cloud Dataflow join-library Authors
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

package com.google.cloud.dataflow.contrib.joinlibrary;

import com.google.cloud.dataflow.sdk.Pipeline;
import com.google.cloud.dataflow.sdk.testing.DataflowAssert;
import com.google.cloud.dataflow.sdk.testing.TestPipeline;
import com.google.cloud.dataflow.sdk.transforms.Create;
import com.google.cloud.dataflow.sdk.values.KV;
import com.google.cloud.dataflow.sdk.values.PCollection;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * This test Outer Right Join functionality.
 */
public class OuterRightJoinTest {

  Pipeline p;
  List<KV<String, Long>> leftListOfKv;
  List<KV<String, String>> listRightOfKv;
  List<KV<String, KV<Long, String>>> expectedResult;

  @Before
  public void setup() {

    p = TestPipeline.create();
    leftListOfKv = new ArrayList<>();
    listRightOfKv = new ArrayList<>();

    expectedResult = new ArrayList<>();
  }

  @Test
  public void testJoinOneToOneMapping() {
    leftListOfKv.add(KV.of("Key1", 5L));
    leftListOfKv.add(KV.of("Key2", 4L));
    PCollection<KV<String, Long>> leftCollection = p.apply(Create.of(leftListOfKv));

    listRightOfKv.add(KV.of("Key1", "foo"));
    listRightOfKv.add(KV.of("Key2", "bar"));
    PCollection<KV<String, String>> rightCollection = p.apply(Create.of(listRightOfKv));

    PCollection<KV<String, KV<Long, String>>> output = Join.rightOuterJoin(
      leftCollection, rightCollection, -1L);

    expectedResult.add(KV.of("Key1", KV.of(5L, "foo")));
    expectedResult.add(KV.of("Key2", KV.of(4L, "bar")));
    DataflowAssert.that(output).containsInAnyOrder(expectedResult);

    p.run();
  }

  @Test
  public void testJoinOneToManyMapping() {
    leftListOfKv.add(KV.of("Key2", 4L));
    PCollection<KV<String, Long>> leftCollection = p.apply(Create.of(leftListOfKv));

    listRightOfKv.add(KV.of("Key2", "bar"));
    listRightOfKv.add(KV.of("Key2", "gazonk"));
    PCollection<KV<String, String>> rightCollection = p.apply(Create.of(listRightOfKv));

    PCollection<KV<String, KV<Long, String>>> output = Join.rightOuterJoin(
      leftCollection, rightCollection, -1L);

    expectedResult.add(KV.of("Key2", KV.of(4L, "bar")));
    expectedResult.add(KV.of("Key2", KV.of(4L, "gazonk")));
    DataflowAssert.that(output).containsInAnyOrder(expectedResult);

    p.run();
  }

  @Test
  public void testJoinManyToOneMapping() {
    leftListOfKv.add(KV.of("Key2", 4L));
    leftListOfKv.add(KV.of("Key2", 6L));
    PCollection<KV<String, Long>> leftCollection = p.apply(Create.of(leftListOfKv));

    listRightOfKv.add(KV.of("Key2", "bar"));
    PCollection<KV<String, String>> rightCollection = p.apply(Create.of(listRightOfKv));

    PCollection<KV<String, KV<Long, String>>> output = Join.rightOuterJoin(
      leftCollection, rightCollection, -1L);

    expectedResult.add(KV.of("Key2", KV.of(4L, "bar")));
    expectedResult.add(KV.of("Key2", KV.of(6L, "bar")));
    DataflowAssert.that(output).containsInAnyOrder(expectedResult);

    p.run();
  }

  @Test
  public void testJoinNoneToOneMapping() {
    leftListOfKv.add(KV.of("Key2", 4L));
    PCollection<KV<String, Long>> leftCollection = p.apply(Create.of(leftListOfKv));

    listRightOfKv.add(KV.of("Key3", "bar"));
    PCollection<KV<String, String>> rightCollection = p.apply(Create.of(listRightOfKv));

    PCollection<KV<String, KV<Long, String>>> output = Join.rightOuterJoin(
      leftCollection, rightCollection, -1L);

    expectedResult.add(KV.of("Key3", KV.of(-1L, "bar")));
    DataflowAssert.that(output).containsInAnyOrder(expectedResult);
    p.run();
  }

  @Test(expected = NullPointerException.class)
  public void testJoinLeftCollectionNull() {
    Join.rightOuterJoin(null, p.apply(Create.of(listRightOfKv)), "");
  }

  @Test(expected = NullPointerException.class)
  public void testJoinRightCollectionNull() {
    Join.rightOuterJoin(p.apply(Create.of(leftListOfKv)), null, -1L);
  }

  @Test(expected = NullPointerException.class)
  public void testJoinNullValueIsNull() {
    Join.rightOuterJoin(p.apply(Create.of(leftListOfKv)), p.apply(Create.of(listRightOfKv)), null);
  }
}
