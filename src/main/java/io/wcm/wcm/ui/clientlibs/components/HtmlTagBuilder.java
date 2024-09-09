/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2023 wcm.io
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

import java.util.Map;
import java.util.TreeMap;

import org.apache.sling.xss.XSSAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class HtmlTagBuilder {

  private final String tagName;
  private final boolean closeTag;
  private final XSSAPI xssApi;
  private final Map<String, String> attrs = new TreeMap<>();

  /**
   * @param tagName Tag name
   * @param closeTag Add closing for HTML tag
   * @param xssApi XSS API
   */
  HtmlTagBuilder(@NotNull String tagName, boolean closeTag, @NotNull XSSAPI xssApi) {
    this.tagName = tagName;
    this.closeTag = closeTag;
    this.xssApi = xssApi;
  }

  /**
   * Set HTML attributes
   * @param attrs HTML attributes
   */
  void setAttrs(Map<String, String> attrs) {
    this.attrs.putAll(attrs);
  }

  /**
   * Set HTML attribute
   * @param name Name
   * @param value Value or null
   */
  void setAttr(@NotNull String name, @Nullable String value) {
    this.attrs.put(name, value);
  }

  /**
   * @return HTML markup
   */
  String build() {
    StringBuilder markup = new StringBuilder();
    markup.append("<").append(tagName);
    for (Map.Entry<String, String> attr : this.attrs.entrySet()) {
      markup.append(" ").append(attr.getKey());
      if (attr.getValue() != null) {
        markup.append("=\"")
            .append(xssApi.encodeForHTMLAttr(attr.getValue()))
            .append("\"");
      }
    }
    markup.append(">");
    if (closeTag) {
      markup.append("</").append(tagName).append(">");
    }
    markup.append("\n");
    return markup.toString();
  }

}
