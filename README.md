# x-user-defined java implementation

Implementation of
[the X-User-Defined charset](https://encoding.spec.whatwg.org/#x-user-defined)
in java.

This charset defined by the W3C is implemented by all major browsers.
It allows lossless storage of binary data as unicode strings.
It is ASCII-compatible and stores each byte in the binary data as the lower
byte of the unicode code points in the encoded string.

It is (surprisingly) a valid and standard charset that can be use in web pages.