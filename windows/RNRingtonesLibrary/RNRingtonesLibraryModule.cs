using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Ringtones.Library.RNRingtonesLibrary
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNRingtonesLibraryModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNRingtonesLibraryModule"/>.
        /// </summary>
        internal RNRingtonesLibraryModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNRingtonesLibrary";
            }
        }
    }
}
