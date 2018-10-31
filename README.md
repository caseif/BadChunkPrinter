# BadChunkPrinter

Tiny utility mod for Sponge which uses Mixin to print the location of a chunk should it fail to load.

The mod works by injecting an error handler into the chunk-loading routine. The error will pass through
and the server will still crash, but the mod will print the location in chunk coordinates prior to the
crash occurring.

## License

This software is made available under the MIT license.
