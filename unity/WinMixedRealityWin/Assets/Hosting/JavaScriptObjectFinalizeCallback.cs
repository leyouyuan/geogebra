﻿namespace ChakraHost.Hosting
{
    #if ENABLE_WINMD_SUPPORT
    using System;

    /// <summary>
    ///     A finalization callback.
    /// </summary>
    /// <param name="data">
    ///     The external data that was passed in when creating the object being finalized.
    /// </param>
    public delegate void JavaScriptObjectFinalizeCallback(IntPtr data);
#endif
}
