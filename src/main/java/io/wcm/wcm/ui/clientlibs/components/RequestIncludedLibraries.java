/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2024 wcm.io
 * %%
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
 * #L%
 */
package io.wcm.wcm.ui.clientlibs.components;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.Nullable;

import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.drew.lang.annotations.NotNull;

class RequestIncludedLibraries {

  /**
   * Request attributes that is also used by Granite UI clientlib manager to store the (raw) library
   * paths that are already included in the current request.
   */
  private static final String RA_INCLUDED_LIBRARY_PATHS = HtmlLibraryManager.class.getName() + ".included";

  private final SlingHttpServletRequest request;
  private final boolean allowMultipleIncludes;

  RequestIncludedLibraries(@NotNull SlingHttpServletRequest request,
      @Nullable Object allowMultipleIncludes) {
    this.request = request;
    this.allowMultipleIncludes = toBoolean(allowMultipleIncludes);
  }

  /**
   * Gets set of library paths from request attribute. If not set, attribute is initialized with an empty set.
   * @return Set of library paths attached to current request
   */
  @SuppressWarnings("unchecked")
  private @NotNull Set<String> getLibaryPathsSetFromRequest() {
    Set<String> libraryPaths = (Set<String>)request.getAttribute(RA_INCLUDED_LIBRARY_PATHS);
    if (libraryPaths == null) {
      libraryPaths = new HashSet<>();
      request.setAttribute(RA_INCLUDED_LIBRARY_PATHS, libraryPaths);
    }
    return libraryPaths;
  }

  /**
   * @param libraryPath Library path
   * @return true if given library was already included in current request.
   */
  boolean isInlucded(@NotNull String libraryPath) {
    return getLibaryPathsSetFromRequest().contains(libraryPath);
  }

  /**
   * Store library path as included in current request.
   * @param libraryPath Library path
   */
  void storeIncluded(@NotNull String libraryPath) {
    getLibaryPathsSetFromRequest().add(libraryPath);
  }

  /**
   * Builds the markup for all given HTML libraries that are not already included in the current request.
   * @param libraryPaths Library paths
   * @param htmlTagBuilderFactory Factory to create HTML tag builders
   * @return Markup
   */
  String buildMarkupIgnoringDuplicateLibraries(@NotNull List<String> libraryPaths,
      @NotNull Function<String, HtmlTagBuilder> htmlTagBuilderFactory) {
    StringBuilder markup = new StringBuilder();
    for (String libraryPath : libraryPaths) {
      // ignore libraries that are already included
      if (!allowMultipleIncludes && isInlucded(libraryPath)) {
        continue;
      }
      // build markup for library
      markup.append(htmlTagBuilderFactory.apply(libraryPath).build());
      // mark library as included
      storeIncluded(libraryPath);
    }
    return markup.toString();
  }

  private static boolean toBoolean(Object value) {
    if (value instanceof Boolean) {
      return (Boolean)value;
    }
    else if (value instanceof String) {
      return BooleanUtils.toBoolean((String)value);
    }
    return false;
  }

}
