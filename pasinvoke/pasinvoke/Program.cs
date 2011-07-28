using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace pasinvoke
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main(string[] args)
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            
            if (args.Length < 1)
                Application.Run(new StoreShortcut());
            else
                Application.Run(new Invoke(args[0], args[1], args[2], args[3], long.Parse(args[4]), int.Parse(args[5]), int.Parse(args[6]), args[7]));
            
            //Application.Run(new Invoke("rw", "umspas", "rw", "2000000000000108", 5000000000001636, 50393, 51468));
        }
    }
}
