package org.apache.mahout.utils.regex;
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


/**
 *
 *
 **/
public class RegexUtilsTest extends MahoutTestCase {
  public static final String[] TEST_STRS = new String[]{
          "127.0.0.1 -  -  [01/10/2011:00:01:51 +0000] \"GET /solr/collection1/browse?q=foo&rows=10&wt=json&hl=true&hl.fl=body&hl.fl=content",
          "127.0.0.1 -  -  [01/10/2011:00:20:58 +0000] \"GET /solr/collection1/browse?q=Using+Solr+Search+RDBMS&fq=%7B%21tag%3Dsource%7D%28%28source%3Alucid+AND+lucid_facet%3A%28site%29%29%29&rows=10",
          "127.0.0.1 -  -  [01/10/2011:00:21:21 +0000] \"GET /solr/collection1/browse?q=language+detection&start=560&rows=10 HTTP/1.1\" 200 45071",
          "127.0.0.1 -  -  [01/10/2011:00:21:21 +0000] \"GET /solr/collection1/browse?q=&start=560&rows=10 HTTP/1.1\" 200 45071"
  };
  public static final String[] GOLD = new String[]{"foo", "Using Solr Search RDBMS", "language detection", ""};

  @Test
  public void testExtract() throws Exception {
    String line = "127.0.0.1 -  -  [24/05/2010:01:19:22 +0000] \"GET /solr/select?q=import statement&start=1 HTTP/1.1\" 200 37571";
    String res;
    Pattern pattern;
    pattern = Pattern.compile("(?<=(\\?|&)q=).*?(?=&|$)");
    res = RegexUtils.extract(line, pattern, Collections.<Integer>emptyList(), " ", RegexUtils.IDENTITY_TRANSFORMER);
    assertTrue(res, res.equals("import statement"));

    for (int i = 0; i < TEST_STRS.length; i++) {
      String testStr = TEST_STRS[i];
      res = RegexUtils.extract(testStr, pattern, Collections.<Integer>emptyList(), " ", new URLDecodeTransformer());
      assertEquals(GOLD[i], res);
    }

    pattern = Pattern.compile("((?<=(\\?|&)q=)(.*?)(?=(&|$))|(?<=((\\?|&)start=))(\\d+))");
    res = RegexUtils.extract(line, pattern, Collections.<Integer>emptyList(), " ", RegexUtils.IDENTITY_TRANSFORMER);
    assertTrue(res, res.equals("import statement 1"));

    pattern = Pattern.compile("(start=1) HTTP");
    List<Integer> groupsToKeep = new ArrayList<Integer>();
    groupsToKeep.add(1);
    res = RegexUtils.extract(line, pattern, groupsToKeep, " ", RegexUtils.IDENTITY_TRANSFORMER);
    assertTrue(res, res.equals("start=1"));
  }
}
