/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2019 wcm.io
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.xss.XSSAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;

/**
 * Include CSS client libraries.
 */
@Model(adaptables = SlingHttpServletRequest.class)
@ProviderType
public class CSSInclude {

  private static final Set<String> REL_ALLOWED_VALUES = Set.of(
      "prefetch", "preload");

  @SlingObject
  private SlingHttpServletRequest request;
  @SlingObject
  private ResourceResolver resourceResolver;
  @OSGiService
  private HtmlLibraryManager htmlLibraryManager;
  @OSGiService
  private XSSAPI xssApi;

  @RequestAttribute(injectionStrategy = InjectionStrategy.OPTIONAL)
  private Object categories;
  @RequestAttribute(injectionStrategy = InjectionStrategy.OPTIONAL)
  private String rel;
  @RequestAttribute(injectionStrategy = InjectionStrategy.OPTIONAL)
  private Object allowMultipleIncludes;
  @RequestAttribute(injectionStrategy = InjectionStrategy.OPTIONAL)
  private Object customAttributes;

  private String include;

  @PostConstruct
  private void activate() {
    // build include string
    String[] categoryArray = IncludeUtil.toArray(categories);
    if (categoryArray != null) {
      List<String> libraryPaths = IncludeUtil.getLibraryUrls(htmlLibraryManager, resourceResolver,
          categoryArray, LibraryType.CSS);
      if (!libraryPaths.isEmpty()) {
        Map<String, String> attrs = validateAndBuildAttributes();
        Map<String, String> customAttrs = IncludeUtil.getCustomAttributes(customAttributes);
        this.include = buildIncludeString(libraryPaths, attrs, customAttrs);
      }
    }
  }

  /**
   * Validate attribute values from HTL script, escape them properly and build a map with all attributes
   * for the resulting script tag(s).
   * @return Map with attribute for script tag
   */
  private @NotNull Map<String, String> validateAndBuildAttributes() {
    Map<String, String> attrs = new HashMap<>();
    if (rel != null && REL_ALLOWED_VALUES.contains(rel)) {
      attrs.put("rel", rel);
    }
    else {
      // no specific rel defined, provide default rel/type attrs for CSS
      attrs.put("rel", "stylesheet");
      attrs.put("type", "text/css");
    }
    return attrs;
  }

  /**
   * Build CSS link tags for all client libraries with the defined custom script tag attributes set.
   * @param libraryPaths Library paths
   * @param attrs HTML attributes for link tag
   * @param customAttrs Custom HTML attributes for script tag
   * @return HTML markup with script tags
   */
  private @NotNull String buildIncludeString(@NotNull List<String> libraryPaths, @NotNull Map<String, String> attrs,
      @NotNull Map<String, String> customAttrs) {
    return new RequestIncludedLibraries(request, allowMultipleIncludes)
        .buildMarkupIgnoringDuplicateLibraries(libraryPaths, libraryPath -> {
          HtmlTagBuilder builder = new HtmlTagBuilder("link", false, xssApi);
          builder.setAttrs(attrs);
          builder.setAttrs(customAttrs);
          builder.setAttr("href", IncludeUtil.appendRequestPath(libraryPath, request));
          return builder;
        });
  }

  /**
   * @return HTML markup with script tags
   */
  public @Nullable String getInclude() {
    return include;
  }

}
