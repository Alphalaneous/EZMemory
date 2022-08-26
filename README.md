# EZMemory

Easy Memory Reading and Writing using **JNA**

## Example: Reading Memory

    MemoryHelper geometryDashMemory = new MemoryHelper("GeometryDash.exe" /* exe name */ , 0x3222d0 /* base address */);  
      
    int[] xPosOffsets = new int[]{0x164, 0x224, 0x67C};  
    int[] levelNameOffsets = new int[]{0x164, 0x22C, 0x114, 0xFC};  
      
    float xPos = geometryDashMemory.read(xPosOffsets, 4 /* how many bytes to read */).getFloat(0);  
    String levelName = geometryDashMemory.readString(levelNameOffsets);

## Example: Writing Memory

    MemoryHelper geometryDashMemory = new MemoryHelper("GeometryDash.exe" /*exe name*/ , 0x3222d0 /* base address*/);  
      
    int[] xPosOffsets = new int[]{0x164, 0x224, 0x67C};  
    int[] yPosOffsets = new int[]{0x164, 0x224, 0x680};  
      
    geometryDashMemory.writeFloat(xPosOffsets, 69f /* bytes to write */);  
    geometryDashMemory.writeFloat(yPosOffsets, 420f /* bytes to write */);

## Example: Injecting a DLL

    MemoryHelper geometryDashMemory = new MemoryHelper("GeometryDash.exe" /*exe name*/ , 0x3222d0 /* base address */);  
      
    geometryDashMemory.injectDLL("C:/Users/Ashton/Documents/coolMod.dll");

## Methods

SmartyPants converts ASCII punctuation characters into "smart" typographic punctuation HTML entities. For example:

|Method                         |          Purpose            |
|-------------------------------|-----------------------------|
|`getProcess()`                 |Returns the Process Handle   |
|`reload()`                     |Resets the Process Handle (Useful if program closes and has reopened)           														|
|`getAppBase()`                 |Returns the dynamic base address|
|`getPID()`											|Returns the process PID			|
|`getExeName()`									|Returns the exe name of the process |
|`isOpen()`											|Returns true if the process is open |
|`isInFocus()`								  |Returns whether the process is in focus or not |
|`read(int[] offsets, int bytesToRead)` \|\| `read(long address, int bytesToRead)` | Reads a value from memory | 
|`readString(long address)` \|\| `readString(int[] offsets)` | Reads a String from memory | 
|`writeBytes(int[] offsets, byte[] value)` | Writes bytes to offsets|
|`writeInt(int[] offsets, int value)` | Writes an int to offsets|
|`writeFloat(int[] offsets, float value)` | Writes a float to offsets|
|`writeString(int[] offsets, String value)` | Writes a String to offsets|
|`writeToAddress(long address, byte[] data)` | Writes bytes to a memory address|
|`injectDLL(String dllLocation)` | Injects a DLL into the process |
