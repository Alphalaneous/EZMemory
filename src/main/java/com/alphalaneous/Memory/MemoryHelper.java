package com.alphalaneous.Memory;

import com.alphalaneous.Exceptions.OSNotSupportedException;
import com.alphalaneous.InjectionStatus;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.W32APIOptions;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.sun.jna.platform.win32.WinNT.PROCESS_QUERY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.PROCESS_VM_READ;

public class MemoryHelper {

    private static final com.sun.jna.platform.win32.Kernel32 kernel32 = Native.loadLibrary("kernel32", com.sun.jna.platform.win32.Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);
    private static final Kernel32 kernel32b = Native.loadLibrary("kernel32", Kernel32.class, W32APIOptions.ASCII_OPTIONS);
    private static final User32 user32 = Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

    private final long base;
    private final String exeName;
    private int PID;
    private long appBase;
    private boolean isOpen = false;
    private WinNT.HANDLE hProcess;

    public MemoryHelper(String exeName, long base){
        this.exeName = exeName;
        this.base = base;
        if(!System.getProperty("os.name").toLowerCase().startsWith("windows")){
            throw new OSNotSupportedException(System.getProperty("os.name") + " is not supported by Dash4j");
        }
        hProcess = openProcess();
    }

    public boolean reload(){
        hProcess = openProcess();
        return hProcess != null;
    }

    public WinNT.HANDLE getProcess(){
        return hProcess;
    }

    public long getAppBase(){
        return appBase;
    }

    public long getBase(){
        return base;
    }

    public int getPID(){
        return PID;
    }

    public String getExeName(){
        return exeName;
    }

    public boolean isOpen(){
        return isOpen;
    }

    public boolean isInFocus(){
        WinDef.HWND windowHandle = user32.GetForegroundWindow();
        IntByReference pid= new IntByReference();
        user32.GetWindowThreadProcessId(windowHandle, pid);
        WinNT.HANDLE processHandle=kernel32.OpenProcess(PROCESS_VM_READ | PROCESS_QUERY_INFORMATION, true, pid.getValue());

        char[] filename = new char[512];
        Psapi.INSTANCE.GetModuleFileNameExW(processHandle, null, filename, filename.length);
        String name = new String(filename);
        name = name.replace("\0", "");

        return name.endsWith(exeName);
    }

    public Memory read(int[] offsets, int bytesToRead) {
        long addr = findDynAddress(offsets, appBase);
        return readMemory(addr, bytesToRead);
    }

    public String readString(long address){
        return getString(GetModuleBaseAddress(PID) + address);
    }
    
    public String readString(int[] offsets){
        long addr = findDynAddress(offsets, appBase);
        return getString(addr);
    }
    
    private String getString(long address) {
        int length = readMemory(address + 0x10, 32).getInt(0);

        String string;

        try {
            if (length >= 16) string = readMemory(readMemory(address, 32).getInt(0), length).getString(0);
            else string = readMemory(address, 32).getString(0).substring(0, length);
        }
        catch (StringIndexOutOfBoundsException e){
            return null;
        }
        if(string.length() < length) return null;
        else string = string.substring(0, length);

        if(string.equalsIgnoreCase("")) return null;

        return string;
    }

    private Memory readMemory(long address, int bytesToRead) {
        IntByReference read = new IntByReference(0);
        Memory output = new Memory(bytesToRead);
        kernel32.ReadProcessMemory(hProcess, Pointer.createConstant(address), output, bytesToRead, read);
        return output;
    }

    public void writeBytes(int[] offsets, byte[] value){
        writeMemory(offsets, value);
    }

    public void writeInt(int[] offsets, int value){
        writeMemory(offsets, ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(value).array());
    }

    public void writeFloat(int[] offsets, float value){
        writeMemory(offsets, ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putFloat(value).array());
    }

    public void writeString(int[] offsets, String value){
        writeMemory(offsets, value.getBytes());
    }

    public void writeToAddress(long address, byte[] data){
        address = GetModuleBaseAddress(PID) + address;
        int size = data.length;
        Memory toWrite = new  Memory(size);
        for(int i = 0; i < size; i++) toWrite.setByte(i, data[i]);
        kernel32.WriteProcessMemory(hProcess, Pointer.createConstant(address), toWrite, size, null);
    }

    private void writeMemory(int[] offsets, byte[] data) {
        long addr = findDynAddress(offsets, appBase);
        int size = data.length;
        Memory toWrite = new  Memory(size);
        for(int i = 0; i < size; i++) toWrite.setByte(i, data[i]);
        kernel32.WriteProcessMemory(hProcess, Pointer.createConstant(addr), toWrite, size, null);
    }

    private long findDynAddress(int[] offsets, long baseAddress) {
        Pointer pointer = new Pointer(baseAddress);
        int size = 4;
        Memory pTemp = new Memory(size);
        long pointerAddress = 0;

        for(int i = 0; i < offsets.length; i++) {
            if(i == 0) kernel32.ReadProcessMemory(hProcess, pointer, pTemp, size, null);
            pointerAddress = pTemp.getInt(0)+offsets[i];
            if(i != offsets.length-1) kernel32.ReadProcessMemory(hProcess, new Pointer(pointerAddress), pTemp, size, null);
        }
        return pointerAddress;
    }


    private WinNT.HANDLE openProcess() {
        WinNT.HANDLE snapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
        Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
        while (kernel32.Process32Next(snapshot, processEntry)) if(new String(processEntry.szExeFile).equalsIgnoreCase(exeName)) break;
        kernel32.CloseHandle(snapshot);
        if(!new String(processEntry.szExeFile).equalsIgnoreCase(exeName)) {
            isOpen = false;
            return null;
        }
        isOpen = true;
        PID = processEntry.th32ProcessID.intValue();
        appBase = GetModuleBaseAddress(processEntry.th32ProcessID.intValue()) + base;
        return kernel32.OpenProcess(56, true, processEntry.th32ProcessID.intValue());
    }

    private long GetModuleBaseAddress(int procID){
        WinDef.DWORD pid = new WinDef.DWORD(procID);
        WinNT.HANDLE hSnap = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, pid);
        Tlhelp32.MODULEENTRY32W module = new Tlhelp32.MODULEENTRY32W();

        while(com.sun.jna.platform.win32.Kernel32.INSTANCE.Module32NextW(hSnap, module)) {
            String s = Native.toString(module.szModule);
            if(s.equals(exeName)){
                Pointer x = module.modBaseAddr;
                return Pointer.nativeValue(x);
            }
        }
        kernel32.CloseHandle(hSnap);
        return -1;
    }

    public InjectionStatus injectDLL(String dllName) {

        BaseTSD.DWORD_PTR loadLibraryAddress = kernel32b.GetProcAddress(kernel32b.GetModuleHandle("KERNEL32"), "LoadLibraryA");
        if(loadLibraryAddress.intValue() == 0) return InjectionStatus.isFailure("Could not find LoadLibrary", kernel32b.GetLastError());
        
        WinDef.LPVOID dllNameAddress = kernel32b.VirtualAllocEx(hProcess, null, (dllName.length() + 1), new BaseTSD.DWORD_PTR(0x3000), new BaseTSD.DWORD_PTR(0x4));
        if(dllNameAddress == null) return InjectionStatus.isFailure("dllNameAddress is null", kernel32b.GetLastError());

        Pointer m = new Memory(dllName.length() + 1);
        m.setString(0, dllName);

        boolean wpmSuccess = kernel32b.WriteProcessMemory(hProcess, dllNameAddress, m, dllName.length(), null).booleanValue();
        if(!wpmSuccess) return InjectionStatus.isFailure("WriteProcessMemory failed", kernel32b.GetLastError());

        BaseTSD.DWORD_PTR threadHandle = kernel32b.CreateRemoteThread(hProcess, 0, 0, loadLibraryAddress, dllNameAddress, 0, 0);
        if(threadHandle.intValue() == 0) return InjectionStatus.isFailure("threadHandle is invalid", kernel32b.GetLastError());

        kernel32.CloseHandle(hProcess);
        return InjectionStatus.isSuccess();
    }
}
