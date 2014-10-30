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

package org.apache.flink.streaming.api.function.aggregation;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.flink.api.common.typeinfo.BasicArrayTypeInfo;
import org.apache.flink.api.common.typeinfo.PrimitiveArrayTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;

public abstract class SumAggregationFunction<T> extends AggregationFunction<T> {

	private static final long serialVersionUID = 1L;

	public SumAggregationFunction(int[] pos, TypeInformation<?> type) {
		super(pos, type);
	}

	@Override
	public T reduce(T value1, T value2) throws Exception {
		return reduce(value1, value2, position, typeInfo);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private T reduce(T field1, T field2, int[] pos,
			TypeInformation<?> typeInfo) {

		if (pos.length == 1) {			
			if (typeInfo.isTupleType()) {
				Tuple tuple1 = (Tuple) field1;
				Tuple tuple2 = (Tuple) field2;

				Tuple returnTuple = tuple2;
				returnTuple.setField(
						add(tuple1.getField(pos[0]),
								tuple2.getField(pos[0])), pos[0]);

				return (T) returnTuple;
			} else if (typeInfo instanceof BasicArrayTypeInfo
					|| typeInfo instanceof PrimitiveArrayTypeInfo) {
				Object v1 = Array.get(field1, pos[0]);
				Object v2 = Array.get(field2, pos[0]);
				Array.set(field2, pos[0], add(v1, v2));
				return field2;
			} else {
				return (T) add(field1, field2);
			}
		} else {
			if (typeInfo.isTupleType()) {
				Tuple tuple1 = (Tuple) field1;
				Tuple tuple2 = (Tuple) field2;

				Tuple returnTuple = tuple2;
				returnTuple.setField(
						reduce((T) tuple1.getField(pos[0]),
								(T) tuple2.getField(pos[0]),
								Arrays.copyOfRange(pos, 1, pos.length),
								((TupleTypeInfo) typeInfo).getTypeAt(pos[0])),
								pos[0]);

				return (T) returnTuple;
			} else if (typeInfo instanceof BasicArrayTypeInfo
					|| typeInfo instanceof PrimitiveArrayTypeInfo) {
				Object v1 = Array.get(field1, pos[0]);
				Object v2 = Array.get(field2, pos[0]);
				Array.set(field2, pos[0], reduce(
						(T)v1, (T)v2,
						Arrays.copyOfRange(pos, 1, pos.length),
						((BasicArrayTypeInfo) typeInfo).getComponentInfo()
					)
				);
				return field2;
			}
		}
		return field2;
	}

	protected abstract Object add(Object value1, Object value2);

	@SuppressWarnings("rawtypes")
	public static <T> SumAggregationFunction getSumFunction(int[] pos,
			Class<T> classAtPos, TypeInformation<?> typeInfo) {

		if (classAtPos == Integer.class) {
			return new IntSum<T>(pos, typeInfo);
		} else if (classAtPos == Long.class) {
			return new LongSum<T>(pos, typeInfo);
		} else if (classAtPos == Short.class) {
			return new ShortSum<T>(pos, typeInfo);
		} else if (classAtPos == Double.class) {
			return new DoubleSum<T>(pos, typeInfo);
		} else if (classAtPos == Float.class) {
			return new FloatSum<T>(pos, typeInfo);
		} else if (classAtPos == Byte.class) {
			return new ByteSum<T>(pos, typeInfo);
		} else {
			throw new RuntimeException(
					"DataStream cannot be summed because the class "
							+ classAtPos.getSimpleName()
							+ " does not support the + operator.");
		}

	}

	private static class IntSum<T> extends SumAggregationFunction<T> {
		private static final long serialVersionUID = 1L;

		public IntSum(int[] pos, TypeInformation<?> type) {
			super(pos, type);
		}

		@Override
		protected Object add(Object value1, Object value2) {
			return (Integer) value1 + (Integer) value2;
		}
	}

	private static class LongSum<T> extends SumAggregationFunction<T> {
		private static final long serialVersionUID = 1L;

		public LongSum(int[] pos, TypeInformation<?> type) {
			super(pos, type);
		}

		@Override
		protected Object add(Object value1, Object value2) {
			return (Long) value1 + (Long) value2;
		}
	}

	private static class DoubleSum<T> extends SumAggregationFunction<T> {

		private static final long serialVersionUID = 1L;

		public DoubleSum(int[] pos, TypeInformation<?> type) {
			super(pos, type);
		}

		@Override
		protected Object add(Object value1, Object value2) {
			return (Double) value1 + (Double) value2;
		}
	}

	private static class ShortSum<T> extends SumAggregationFunction<T> {
		private static final long serialVersionUID = 1L;

		public ShortSum(int[] pos, TypeInformation<?> type) {
			super(pos, type);
		}

		@Override
		protected Object add(Object value1, Object value2) {
			return (Short) value1 + (Short) value2;
		}
	}

	private static class FloatSum<T> extends SumAggregationFunction<T> {
		private static final long serialVersionUID = 1L;

		public FloatSum(int[] pos, TypeInformation<?> type) {
			super(pos, type);
		}

		@Override
		protected Object add(Object value1, Object value2) {
			return (Float) value1 + (Float) value2;
		}
	}

	private static class ByteSum<T> extends SumAggregationFunction<T> {
		private static final long serialVersionUID = 1L;

		public ByteSum(int[] pos, TypeInformation<?> type) {
			super(pos, type);
		}

		@Override
		protected Object add(Object value1, Object value2) {
			return (Byte) value1 + (Byte) value2;
		}
	}

}
