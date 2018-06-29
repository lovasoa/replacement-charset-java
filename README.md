# *replacement* charset implementation in java

Implementation of
[the replacement charset](https://encoding.spec.whatwg.org/#replacement)
in java.

This charset defined by the W3C is implemented by all major browsers.

According to the W3C:

> The replacement encoding exists to prevent certain attacks
> that abuse a mismatch between encodings supported on the server
> and the client. 

This dummy charset is **not ASCII-compatible**, has only a decoder,
and decodes any byte stream to a single '�' (0+FFFD) replacement character.

It is used in web standards as a replacement charset for the following
legacy charsets:

 * csiso2022kr
 * hz-gb-2312
 * iso-2022-cn
 * iso-2022-cn-ext
 * iso-2022-kr

This java implementation could be used to decode web pages that were served in
one of the above charsets, in order to decode the pages in exactly the same way
as they would appear in a browser.