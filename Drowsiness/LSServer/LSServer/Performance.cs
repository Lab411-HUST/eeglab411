using System;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;
using System.Threading;
using System.Collections.Generic;
namespace LSServer
{
    public class Performance
    {
        PerformanceCounter[] cpuProcessor;
        PerformanceCounter CPUUsage;
        PerformanceCounter RAMUsage;
        public const int NUM_PROCESSORS = 4;
        public Performance()
        {
            cpuProcessor = new PerformanceCounter[NUM_PROCESSORS];
            for (int i = 0; i < NUM_PROCESSORS; i++)
            {
                cpuProcessor[i] = new PerformanceCounter("Processor", "% Processor Time", i.ToString());
            }
            CPUUsage = new PerformanceCounter("Processor", "% Processor Time", "_Total");
            RAMUsage = new PerformanceCounter("Process", "Working Set - Private", Process.GetCurrentProcess().ProcessName);
        }
        [DllImport("psapi.dll", SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        public static extern bool GetPerformanceInfo([Out] out PerformanceInformation PerformanceInformation, [In] int Size);

        [StructLayout(LayoutKind.Sequential)]
        public struct PerformanceInformation
        {
            public int Size;
            public IntPtr CommitTotal;
            public IntPtr CommitLimit;
            public IntPtr CommitPeak;
            public IntPtr PhysicalTotal;
            public IntPtr PhysicalAvailable;
            public IntPtr SystemCache;
            public IntPtr KernelTotal;
            public IntPtr KernelPaged;
            public IntPtr KernelNonPaged;
            public IntPtr PageSize;
            public int HandlesCount;
            public int ProcessCount;
            public int ThreadCount;
        }

        [DllImport("KERNEL32.DLL", EntryPoint = "SetProcessWorkingSetSize", SetLastError = true, CallingConvention = CallingConvention.StdCall)]
        internal static extern bool SetProcessWorkingSetSize(IntPtr pProcess, int dwMinimumWorkingSetSize, int dwMaximumWorkingSetSize);

        [DllImport("KERNEL32.DLL", EntryPoint = "GetCurrentProcess", SetLastError = true, CallingConvention = CallingConvention.StdCall)]
        internal static extern IntPtr MyGetCurrentProcess();

        // In main():
        //PCPerformance.SetProcessWorkingSetSize(Process.GetCurrentProcess().Handle, int.MaxValue, int.MaxValue);

        public Int64 GetPhysicalAvailableMemory()
        {
            PerformanceInformation pi = new PerformanceInformation();
            if (GetPerformanceInfo(out pi, Marshal.SizeOf(pi)))
            {
                return Convert.ToInt64((pi.PhysicalAvailable.ToInt64() * pi.PageSize.ToInt64() / 1048576));
            }
            else
            {
                return -1;
            }

        }

        public Int64 GetTotalMemory()
        {
            PerformanceInformation pi = new PerformanceInformation();
            if (GetPerformanceInfo(out pi, Marshal.SizeOf(pi)))
            {
                return Convert.ToInt64((pi.PhysicalTotal.ToInt64() * pi.PageSize.ToInt64() / 1048576));
            }
            else
            {
                return -1;
            }

        }
        public int numberOfProcessors()
        {
            return Environment.ProcessorCount;
        }
        public int[] getCPU()
        {
            int[] result = new int[NUM_PROCESSORS];

            for (int i = 0; i < NUM_PROCESSORS; i++)
            {
                result[i] = (int)cpuProcessor[i].NextValue();
            }
            return result;
        }
        public int getTotalCPU()
        {
            return (int)CPUUsage.NextValue();
        }
        public int getMemoryProcess()
        {
            //MB
            int result = (int)(RAMUsage.RawValue) / (1024 * 1024);
            return result;
        }
    }
}
