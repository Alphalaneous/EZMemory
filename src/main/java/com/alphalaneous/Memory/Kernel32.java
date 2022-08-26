package com.alphalaneous.Memory;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {

    public BaseTSD.DWORD_PTR GetProcAddress(WinNT.HANDLE hModule, String  lpProcName);
    public WinDef.LPVOID VirtualAllocEx(WinNT.HANDLE hProcess, WinDef.LPVOID lpAddress, int dwSize, BaseTSD.DWORD_PTR flAllocationType, BaseTSD.DWORD_PTR flProtect);
    public WinDef.BOOL WriteProcessMemory(WinNT.HANDLE hProcess, WinDef.LPVOID lpBaseAddress, Pointer lpBuffer, int nSize, Pointer lpNumberOfBytesWritten);
    public BaseTSD.DWORD_PTR CreateRemoteThread(WinNT.HANDLE hProcess, int lpThreadAttributes, int dwStackSize, BaseTSD.DWORD_PTR loadLibraryAddress, WinDef.LPVOID lpParameter, int dwCreationFlags, int lpThreadId);
    public int GetLastError();
    public WinNT.HANDLE GetModuleHandle(String string);

}
