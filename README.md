This CustomBase64 class provides the functionality to encode/decode a byte array to/from a Base64 String.
It does NOT follow the RFC 3548 or RFC 4648 specification of Base64 as the used charset can be specified
and no padding characters are used.
So only the characters provided by the user by calling the setBase64Charset(String) will appear in the encoded String and no other characters 
like equal signs '=' (sometimes used for padding in Base64) will appear.
