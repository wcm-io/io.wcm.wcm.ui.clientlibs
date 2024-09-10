## Clientlibs UI Extensions usage

### Include JS Files

Include without special attributes:

```html
<sly data-sly-use.clientlib="/apps/wcm-io/wcm/ui/clientlibs/sightly/templates/clientlib.html"
    data-sly-call="${clientlib.js @ categories=['my-clientlib-category']}"/>
```

Include with advanced script tag attributes:

```html
<sly data-sly-use.clientlib="/apps/wcm-io/wcm/ui/clientlibs/sightly/templates/clientlib.html"
    data-sly-call="${clientlib.js @ categories=['my-clientlib-category'],async=true,type='module'}"/>
<sly data-sly-call="${clientlib.js @ categories=['my-clientlib-category-2'],defer=true,nomodule=true}"/>
```

The following advanced script tag attributes are supported:

* `async` = true | false
* `crossorigin` = anonymous | use-credentials
* `defer` = true | false
* `integrity` = {string}
* `nomodule` = true | false
* `nonce` = {string}
* `referrerpolicy` = no-referrer | no-referrer-when-downgrade | origin | origin-when-cross-origin | same-origin | strict-origin | strict-origin-when-cross-origin | unsafe-url
* `type` = module | text/javascript
* `allowMultipleIncludes` - by default, multiple includes of the same script in a single request are eliminated. If setting this property to `true`, they are included multiple times if requested.
* `customAttributes` - set arbitrary HTML attributes, e.g. `customAttributes=['attr1=value 1','data-attr2=5','attr3']`

See https://developer.mozilla.org/en-US/docs/Web/HTML/Element/script#Attributes for a full documentation of this attributes.


### Include CSS Files

Include CSS without special attributes:

```html
<sly data-sly-use.clientlib="/apps/wcm-io/wcm/ui/clientlibs/sightly/templates/clientlib.html"
    data-sly-call="${clientlib.css @ categories=['my-clientlib-category']}"/>
```

The following advanced link tag attributes are supported:

* `rel` = prefetch | preload (if not given, `rel="stylesheet" type="text/css"` is set)
* `allowMultipleIncludes` - by default, multiple includes of the same script in a single request are eliminated. If setting this property to `true`, they are included multiple times if requested.
* `customAttributes` - set arbitrary HTML attributes, e.g. `customAttributes=['attr1=value 1','data-attr2=5','attr3']`
