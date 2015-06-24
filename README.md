# Character Hack
*Convert UTF-8 to Watson-safe ASCII*

Watson Q&A instances do not support UTF-8. Various internal processes and methods used to parse text require ASCII-only text. To correct this the IBM Ingestion team runs several text transformations on documents to make it Watson-safe. Unfortunately, these transformations may not be made public or made available to partners. 

The goal of this project is to transform documents **prior to ingestion** so that documents are not modified during ingestion.

##Known Ingestion Transformations
These are the known transformations done to documents during ingestion. This list may be incomplete.

1. Replace some numerical HTML encoding with actual characters.
Decimal code point characters in HTML are converted to what they represent. For example, `&#038;` is transformed to `&` and `&#045;` to `-`.

2. Handle non-ASCII characters
Non-ASCII characters may have a replacement or be deleted. How individual characters are handled changes irregularly, and the replacements are inconsistent.

3. Remove spaces around hyphens
If a hyphen has spaces around it, those spaces are removed. We know that `a - b` becomes `a-b`, but we have not yet tested what happens to `a- b` or `a -b`.

## How Character Hack works
Currently, this is a single Java class which transforms data so that the transformations run by Ingestion have no effect. It takes a String as input, and outputs the resulting transformed String. The following transformations are done on strings in this order.

1. Replace all numerical HTML encoding with actual characters.

2. Use the [International Components for Unicode](http://site.icu-project.org) (ICU) Transliterator class to make a set of standardized replacements from UTF-8 to pure ASCII. For example, this transforms `©` to `(C)` and `½` to `1/2`.

3. Remove spaces around hyphens with spaces on both sides.

The order here matters. So `a ― b` will be transformed into `a - b` by step 2, then `a-b` by step 3. Similarly, `&#235` will first be transformed into `ë`  by step 1, and then `e` by step 2.

##Usage
Use this on all text before ingesting it into Watson. This will ensure the text you have matches (or at least closely resembles) what is in Watson. 

Character Hack includes a single Java class, CharacterHack. Use the `watsonTransform` function on Strings to convert them to Watson-safe ASCII.

    CharacterHack.transform("I cän ©onvert - things &#038; stuff: ½.")
    
    Output: I can (C)onvert-things & stuff:  1/2.
    
Note the additional space inserted in front of the ½. This was inserted by the ICU library. 