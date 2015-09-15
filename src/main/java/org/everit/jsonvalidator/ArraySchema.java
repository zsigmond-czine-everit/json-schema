/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.jsonvalidator;

import java.util.stream.IntStream;

import org.json.JSONArray;

public class ArraySchema implements Schema {

  public static class Builder {

    private Integer minItems;

    private Integer maxItems;

    private boolean uniqueItems = false;

    public ArraySchema build() {
      return new ArraySchema(this);
    }

    public Builder maxItems(final Integer maxItems) {
      this.maxItems = maxItems;
      return this;
    }

    public Builder minItems(final Integer minItems) {
      this.minItems = minItems;
      return this;
    }

    public Builder uniqueItems(final boolean uniqueItems) {
      this.uniqueItems = uniqueItems;
      return this;
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final Integer minItems;

  private final Integer maxItems;

  private final boolean uniqueItems;

  public ArraySchema(final Builder builder) {
    this.minItems = builder.minItems;
    this.maxItems = builder.maxItems;
    this.uniqueItems = builder.uniqueItems;
  }

  private void testItemCount(final JSONArray subject) {
    int actualLength = subject.length();
    if (minItems != null && actualLength < minItems) {
      throw new ValidationException("expected minimum item count: " + minItems + ", found: "
          + actualLength);
    }
    if (maxItems != null && maxItems < actualLength) {
      throw new ValidationException("expected maximum item count: " + minItems + ", found: "
          + actualLength);
    }
  }

  private void testUniqueness(final JSONArray subject) {
    if (subject.length() == 0) {
      return;
    }
    long uniqueLength = IntStream.range(0, subject.length())
        .mapToObj(subject::get)
        .map(Object::toString)
        .distinct()
        .count();
    if (uniqueLength < subject.length()) {
      throw new ValidationException("array items are not unique");
    }
  }

  @Override
  public void validate(final Object subject) {
    if (!(subject instanceof JSONArray)) {
      throw new ValidationException(JSONArray.class, subject);
    }
    JSONArray arrSubject = (JSONArray) subject;
    testItemCount(arrSubject);
    if (uniqueItems) {
      testUniqueness(arrSubject);
    }
  }
}