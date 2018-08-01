/*-
 * -\-\-
 * Flo BigQuery
 * --
 * Copyright (C) 2016 - 2018 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.flo.contrib.bigquery;

import static com.spotify.flo.contrib.bigquery.BigQueryClientSingleton.bq;

import com.google.cloud.bigquery.JobInfo;
import com.spotify.flo.EvalContext;
import com.spotify.flo.TaskOperator;
import com.spotify.flo.contrib.bigquery.BigQueryOperation.Provider;

public class BigQueryOperator<T> implements TaskOperator<BigQueryOperation.Provider<T>, BigQueryOperation<T, ?>, T> {

  private BigQueryOperator() {
  }

  @Override
  public T perform(BigQueryOperation<T, ?> spec, Listener listener) {
    final Object result;
    if (spec.queryRequest != null) {
      result = runQuery(spec);
    } else if (spec.jobRequest != null) {
      result = runJob(spec);
    } else {
      throw new AssertionError();
    }
    return spec.success.apply(result);
  }

  private JobInfo runJob(BigQueryOperation<T, ?> spec) {
    return bq().job(spec.jobRequest.get());
  }

  private BigQueryResult runQuery(BigQueryOperation<T, ?> spec) {
    return bq().query(spec.queryRequest.get());
  }

  public static <T> BigQueryOperator<T> create() {
    return new BigQueryOperator<>();
  }

  @Override
  public Provider<T> provide(EvalContext evalContext) {
    return new BigQueryOperation.Provider<>();
  }
}
