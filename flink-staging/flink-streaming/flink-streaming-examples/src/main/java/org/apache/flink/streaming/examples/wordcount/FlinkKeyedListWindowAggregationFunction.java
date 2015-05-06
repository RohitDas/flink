/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.examples.wordcount;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.WindowMapFunction;
import org.apache.flink.util.Collector;

public class FlinkKeyedListWindowAggregationFunction implements WindowMapFunction<Tuple2<String, Integer>, Tuple2<String, Iterable<Integer>>> {

    @Override
    public void mapWindow(Iterable<Tuple2<String, Integer>> values, Collector<Tuple2<String, Iterable<Integer>>> out) throws Exception {
        List<Integer> output = new ArrayList<Integer>();
        for(Tuple2<String,Integer> element: values){
        	output.add(element.f1);
        }
        out.collect(new Tuple2<String, Iterable<Integer>>(values.iterator().next().f0, output));
    }
}
